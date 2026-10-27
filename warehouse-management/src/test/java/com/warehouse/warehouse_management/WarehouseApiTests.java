package com.warehouse.warehouse_management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * 后端集成测试：H2 内存库 + MockMvc，不用真实 MySQL 也能跑通全部业务流程。
 *
 * 覆盖点：
 * 1. Spring 上下文能启动 —— 直接证明 findByProductId 那个启动崩溃已修复；
 * 2. 登录 / JWT 鉴权 / 密码不外泄；
 * 3. 分页与关键字搜索；
 * 4. 参数校验与业务异常的错误码；
 * 5. 入库累加库存、撤销回滚库存（事务一致性）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WarehouseApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    // ---------------------------------------------------------------- helpers

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String login(String username, String password) throws Exception {
        JsonNode body = json(mockMvc.perform(post("/user/login")
                .contentType(JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
        ).andReturn());
        assertEquals(200, body.path("code").asInt(), "登录应成功：" + body);
        return body.path("data").path("token").asText();
    }

    private String adminToken() throws Exception {
        return login("admin", "123456");
    }

    private long createProduct(String token, String sku, String name) throws Exception {
        JsonNode body = json(mockMvc.perform(post("/product/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"sku\":\"" + sku + "\",\"name\":\"" + name + "\",\"category\":\"测试\",\"price\":9.99}")
        ).andReturn());
        assertEquals(200, body.path("code").asInt(), "新增商品应成功：" + body);
        return body.path("data").path("id").asLong();
    }

    private long createWarehouse(String token, String code) throws Exception {
        JsonNode body = json(mockMvc.perform(post("/warehouse/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"code\":\"" + code + "\",\"name\":\"测试仓\",\"address\":\"乐山\",\"manager\":\"王嘉懿\"}")
        ).andReturn());
        assertEquals(200, body.path("code").asInt(), "新增仓库应成功：" + body);
        return body.path("data").path("id").asLong();
    }

    private JsonNode detail(String token, String url) throws Exception {
        return json(mockMvc.perform(get(url).header("Authorization", "Bearer " + token)).andReturn());
    }

    // ---------------------------------------------------------------- tests

    @Test
    @DisplayName("上下文启动 + 默认账号可登录 + 响应不含密码")
    void contextLoadsAndLoginWorks() throws Exception {
        JsonNode body = json(mockMvc.perform(post("/user/login")
                .contentType(JSON)
                .content("{\"username\":\"admin\",\"password\":\"123456\"}")
        ).andReturn());

        assertEquals(200, body.path("code").asInt());
        assertFalse(body.path("data").path("token").asText().isBlank(), "应返回 JWT");
        String raw = body.toString();
        assertFalse(raw.contains("password"), "响应里不应该出现 password 字段：" + raw);
        assertFalse(raw.contains("$2a$"), "响应里不应该出现 BCrypt 哈希：" + raw);
    }

    @Test
    @DisplayName("密码错误返回 401 且不暴露内部信息")
    void wrongPasswordIsRejected() throws Exception {
        JsonNode body = json(mockMvc.perform(post("/user/login")
                .contentType(JSON)
                .content("{\"username\":\"admin\",\"password\":\"wrong-pass\"}")
        ).andReturn());

        assertEquals(401, body.path("code").asInt());
        assertEquals("用户名或密码错误", body.path("message").asText());
    }

    @Test
    @DisplayName("不带 token 访问业务接口 → HTTP 401 + code 401")
    void requestWithoutTokenIsUnauthorized() throws Exception {
        MvcResult result = mockMvc.perform(get("/product/list")).andReturn();
        assertEquals(401, result.getResponse().getStatus(), "HTTP 状态码应为 401");
        assertEquals(401, json(result).path("code").asInt());
    }

    @Test
    @DisplayName("伪造 token 同样被拒绝")
    void forgedTokenIsRejected() throws Exception {
        MvcResult result = mockMvc.perform(get("/product/list")
                .header("Authorization", "Bearer not-a-real-token")).andReturn();
        assertEquals(401, result.getResponse().getStatus());
    }

    @Test
    @DisplayName("注册接口不在白名单：不带 token 注册 → 401")
    void registerRequiresAuth() throws Exception {
        MvcResult result = mockMvc.perform(post("/user/register")
                .contentType(JSON)
                .content("{\"username\":\"hacker\",\"password\":\"123456\"}"))
                .andReturn();
        assertEquals(401, result.getResponse().getStatus(), "注册必须要求登录");
    }

    @Test
    @DisplayName("分页 + 关键字搜索")
    void paginationAndSearch() throws Exception {
        String token = adminToken();
        createProduct(token, "SKU-A1", "机械键盘");
        createProduct(token, "SKU-A2", "无线鼠标");
        createProduct(token, "SKU-B1", "显示器支架");

        JsonNode page1 = detail(token, "/product/list?page=1&size=2").path("data");
        assertEquals(3, page1.path("total").asInt(), "总数应为 3");
        assertEquals(2, page1.path("list").size(), "第一页应有 2 条");
        assertEquals(1, page1.path("page").asInt(), "页码从 1 开始");

        JsonNode page2 = detail(token, "/product/list?page=2&size=2").path("data");
        assertEquals(1, page2.path("list").size(), "第二页应有 1 条");

        JsonNode searched = detail(token, "/product/list?keyword=鼠标").path("data");
        assertEquals(1, searched.path("total").asInt(), "关键字搜索应命中 1 条");
        assertEquals("无线鼠标", searched.path("list").get(0).path("name").asText());
    }

    @Test
    @DisplayName("参数校验：商品名称为空返回 400 而不是 500")
    void validationReturns400() throws Exception {
        String token = adminToken();
        JsonNode body = json(mockMvc.perform(post("/product/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"sku\":\"SKU-X\",\"name\":\"\",\"price\":1}")
        ).andReturn());

        assertEquals(400, body.path("code").asInt(), "校验失败应是 400：" + body);
        assertEquals("商品名称不能为空", body.path("message").asText());
    }

    @Test
    @DisplayName("重复 SKU 返回业务错误")
    void duplicateSkuIsRejected() throws Exception {
        String token = adminToken();
        createProduct(token, "SKU-DUP", "商品一");

        JsonNode body = json(mockMvc.perform(post("/product/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"sku\":\"SKU-DUP\",\"name\":\"商品二\",\"price\":1}")
        ).andReturn());

        assertEquals(500, body.path("code").asInt());
        assertTrue(body.path("message").asText().contains("SKU 已存在"), body.toString());
    }

    @Test
    @DisplayName("入库累加库存 —— 原项目这里库存永远不变")
    void inboundIncreasesStock() throws Exception {
        String token = adminToken();
        long productId = createProduct(token, "SKU-STOCK", "库存测试商品");
        long warehouseId = createWarehouse(token, "WH-STOCK");

        assertEquals(0, detail(token, "/product/" + productId).path("data").path("stock").asInt(), "初始库存应为 0");

        JsonNode inbound = json(mockMvc.perform(post("/inbound/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"productId\":" + productId + ",\"warehouseId\":" + warehouseId + ",\"quantity\":30}")
        ).andReturn());
        assertEquals(200, inbound.path("code").asInt(), inbound.toString());

        assertEquals(30, detail(token, "/product/" + productId).path("data").path("stock").asInt(), "入库 30 后库存应为 30");
        assertFalse(inbound.path("data").path("inboundTime").asText().isBlank(), "入库时间应自动填充");

        // 再入 5，累加
        mockMvc.perform(post("/inbound/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"productId\":" + productId + ",\"warehouseId\":" + warehouseId + ",\"quantity\":5}")
        ).andReturn();
        assertEquals(35, detail(token, "/product/" + productId).path("data").path("stock").asInt());
    }

    @Test
    @DisplayName("撤销入库：扣回库存并删除记录")
    void cancelInboundRollsBackStock() throws Exception {
        String token = adminToken();
        long productId = createProduct(token, "SKU-CANCEL", "撤销测试商品");
        long warehouseId = createWarehouse(token, "WH-CANCEL");

        JsonNode inbound = json(mockMvc.perform(post("/inbound/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"productId\":" + productId + ",\"warehouseId\":" + warehouseId + ",\"quantity\":10}")
        ).andReturn());
        long recordId = inbound.path("data").path("id").asLong();
        assertEquals(10, detail(token, "/product/" + productId).path("data").path("stock").asInt());

        mockMvc.perform(delete("/inbound/" + recordId).header("Authorization", "Bearer " + token)).andReturn();
        assertEquals(0, detail(token, "/product/" + productId).path("data").path("stock").asInt(), "撤销后库存应回到 0");

        JsonNode list = detail(token, "/inbound/list?keyword=撤销测试商品").path("data");
        assertEquals(0, list.path("total").asInt(), "记录应已删除");
    }

    @Test
    @DisplayName("编辑商品不会覆盖入库累加出来的库存")
    void updatingProductKeepsStock() throws Exception {
        String token = adminToken();
        long productId = createProduct(token, "SKU-UPD", "编辑库存测试商品");
        long warehouseId = createWarehouse(token, "WH-UPD");

        mockMvc.perform(post("/inbound/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"productId\":" + productId + ",\"warehouseId\":" + warehouseId + ",\"quantity\":25}")
        ).andReturn();
        assertEquals(25, detail(token, "/product/" + productId).path("data").path("stock").asInt());

        // 编辑商品时故意把 stock 传成 0，库存必须保持不变
        JsonNode updated = json(mockMvc.perform(put("/product/" + productId)
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"sku\":\"SKU-UPD\",\"name\":\"编辑后的商品\",\"category\":\"测试\",\"price\":1.50,\"stock\":0}")
        ).andReturn());

        assertEquals(200, updated.path("code").asInt(), updated.toString());
        assertEquals("编辑后的商品", updated.path("data").path("name").asText());
        assertEquals(25, detail(token, "/product/" + productId).path("data").path("stock").asInt(), "编辑商品不应覆盖库存");
    }

    @Test
    @DisplayName("数量校验：quantity=0 被拦截（否则可以刷空记录）")
    void inboundQuantityMustBePositive() throws Exception {
        String token = adminToken();
        long productId = createProduct(token, "SKU-QTY", "数量测试商品");
        long warehouseId = createWarehouse(token, "WH-QTY");

        JsonNode body = json(mockMvc.perform(post("/inbound/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"productId\":" + productId + ",\"warehouseId\":" + warehouseId + ",\"quantity\":0}")
        ).andReturn());

        assertEquals(400, body.path("code").asInt(), body.toString());
    }

    @Test
    @DisplayName("商品不存在时入库给出明确业务错误，而不是 500 堆栈")
    void inboundWithUnknownProductFails() throws Exception {
        String token = adminToken();
        long warehouseId = createWarehouse(token, "WH-404");

        JsonNode body = json(mockMvc.perform(post("/inbound/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"productId\":999999,\"warehouseId\":" + warehouseId + ",\"quantity\":1}")
        ).andReturn());

        assertEquals(500, body.path("code").asInt());
        assertEquals("商品不存在", body.path("message").asText());
    }

    @Test
    @DisplayName("有入库记录的仓库不允许删除")
    void warehouseWithRecordsCannotBeDeleted() throws Exception {
        String token = adminToken();
        long productId = createProduct(token, "SKU-WH", "仓库保护测试商品");
        long warehouseId = createWarehouse(token, "WH-PROTECT");

        mockMvc.perform(post("/inbound/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"productId\":" + productId + ",\"warehouseId\":" + warehouseId + ",\"quantity\":3}")
        ).andReturn();

        JsonNode body = json(mockMvc.perform(delete("/warehouse/delete/" + warehouseId)
                .header("Authorization", "Bearer " + token)).andReturn());

        assertTrue(body.path("message").asText().contains("不能删除"), body.toString());
    }

    @Test
    @DisplayName("删除商品会连带清理它的入库记录（否则外键报错）")
    void deletingProductCleansInboundRecords() throws Exception {
        String token = adminToken();
        long productId = createProduct(token, "SKU-DEL", "待删除商品");
        long warehouseId = createWarehouse(token, "WH-DEL");

        mockMvc.perform(post("/inbound/save")
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .content("{\"productId\":" + productId + ",\"warehouseId\":" + warehouseId + ",\"quantity\":7}")
        ).andReturn();

        JsonNode deleted = json(mockMvc.perform(delete("/product/delete/" + productId)
                .header("Authorization", "Bearer " + token)).andReturn());
        assertEquals(200, deleted.path("code").asInt(), deleted.toString());

        assertEquals(404, detail(token, "/product/" + productId).path("code").asInt(), "商品应已删除");
    }
}
