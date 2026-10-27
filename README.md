# 仓库管理系统（Warehouse Management System）

一个前后端分离的仓库管理后台：管理仓库、商品与入库记录，入库自动累加库存、撤销入库自动扣回。
技术栈刻意选得和企业实习岗常见要求一致（Spring Boot 3 + JPA + MySQL + Vue 3 + Element Plus）。

> 这是我在学习阶段做的第一个完整前后端项目，2025 年 11 月完成初版，2026 年 10 月做了一次整体重构：
> 修掉了一个导致服务无法启动的 ORM 查询 bug，补上了登录鉴权（JWT）、密码加密（BCrypt）、
> 入库与库存的事务一致性、分页搜索和统一异常处理，并补了 H2 集成测试与建表脚本。
> 重构前后的差异见文末「本次重构改了什么」。

---

## 一、技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.5.8、Spring Data JPA、Bean Validation、Lombok |
| 安全 | JJWT 0.12.6（JWT 签发/校验）、spring-security-crypto（BCrypt） |
| 数据库 | MySQL 8.0（H2 内存库用于测试） |
| 前端 | Vue 3.5、Vite 7、Vue Router 4、Pinia 3、Element Plus 2.11、Axios |
| 构建 | Maven、npm |

## 二、功能

- **登录**：账号密码登录，成功后签发 JWT，后续接口通过 `Authorization: Bearer <token>` 鉴权；密码 BCrypt 存储，接口响应永远不含密码。
- **仓库管理**：新增、编辑、分页查询（按编码/名称/地址/管理员搜索）、删除（仓库里有入库记录时拒绝删除）。
- **商品管理**：新增、编辑、分页查询（按 SKU/名称/分类搜索）、删除（连带清理该商品的入库记录）。
- **入库管理**：新增入库（写记录 + 累加商品库存，同一事务）、撤销入库（扣回库存 + 删记录）、分页查询。
- **统一返回与异常**：所有接口返回 `{ code, message, data }`；参数校验失败返回 400 并给出字段级提示；业务异常返回明确文案；未预期的异常统一记录日志并返回 500 兜底文案，不向前端泄漏堆栈。

## 三、目录结构

```
Warehouse-Management/
├── warehouse-management/                 # 后端
│   ├── sql/warehouse_db.sql              # 建表 + 演示数据
│   └── src/
│       ├── main/java/com/warehouse/warehouse_management/
│       │   ├── common/                   # Result / ResultCode / PageResult / BusinessException / 全局异常
│       │   ├── config/                   # 拦截器注册与 CORS、BCrypt Bean、启动时数据初始化
│       │   ├── controller/               # 用户 / 仓库 / 商品 / 入库
│       │   ├── dto/                      # 登录、入库请求与脱敏后的用户信息
│       │   ├── entity/                   # 4 张表的实体
│       │   ├── repository/               # Spring Data JPA 接口
│       │   ├── security/                 # JwtUtil、AuthInterceptor
│       │   └── service/                  # 业务逻辑（事务边界都在这一层）
│       ├── main/resources/application.properties
│       └── test/                         # H2 + MockMvc 集成测试
└── warehouse-frontend/                   # 前端
    └── src/
        ├── components/Layout.vue         # 侧边栏 + 顶栏布局
        ├── router/index.js               # 路由与登录守卫
        ├── store/userStore.js            # token / 用户信息
        ├── utils/request.js              # axios 实例：自动带 token、统一错误处理
        └── views/                        # 登录 / 首页 / 商品 / 仓库 / 入库
```

## 四、快速开始

### 1. 准备数据库

```bash
mysql -uroot -p < warehouse-management/sql/warehouse_db.sql
```

脚本会建库 `warehouse_db`、建 4 张表并写入演示数据（3 个仓库、6 个商品、3 条入库记录）。

### 2. 配置数据库账号

数据库密码不再写死在代码里，通过环境变量传入：

```bash
# Windows PowerShell（当前窗口有效）
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的MySQL密码"

# macOS / Linux
export DB_USERNAME=root
export DB_PASSWORD=你的MySQL密码
```

可选：`JWT_SECRET`（至少 32 字节）、`JWT_EXPIRE_HOURS`（默认 12 小时）。

### 3. 启动后端

```bash
cd warehouse-management
mvn spring-boot:run
# 或 mvnw spring-boot:run
```

启动后自动：①建表/更新表结构（`ddl-auto=update`）；②把历史明文密码就地升级为 BCrypt；
③如果没有 admin 账号，创建 `admin / 123456` 并在日志里提示。

后端地址：`http://localhost:8081/api`

### 4. 启动前端

```bash
cd warehouse-frontend
npm install     # 首次
npm run dev
```

打开 `http://localhost:5173`，用 `admin / 123456` 登录。前端通过 Vite 代理把 `/api` 转发到 8081。

### 5. 跑测试

```bash
cd warehouse-management
mvn test
```

测试用 H2 内存库，不依赖本地 MySQL；覆盖登录鉴权、分页搜索、参数校验、入库加库存、撤销回库存、删除保护等 13 个场景。

### 6. 常见启动报错

| 报错 | 原因 | 处理 |
| --- | --- | --- |
| `Unable to determine Dialect without JDBC metadata` | 连不上数据库（密码没传 / MySQL 没启动） | 确认 MySQL 服务已启动，并已设置 `DB_PASSWORD` 环境变量 |
| `Access denied for user 'root'@'localhost'` | 密码不对 | 改 `DB_PASSWORD`；本机可用 `mysql -uroot -p` 先确认密码 |
| `Port 8081 was already in use` | 端口被占用 | 关掉旧进程，或改 `server.port` |
| 前端页面能打开但接口 401 | token 过期（默认 12 小时） | 重新登录 |
| 前端 `/api` 请求 404 | 后端没起或端口不一致 | 检查 `vite.config.js` 的 proxy target 与 `server.port` |

## 五、接口清单

统一前缀 `/api`，除登录/注册外都需要 `Authorization: Bearer <token>`。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/user/login` | 登录，返回 token 与用户信息 |
| POST | `/user/register` | 注册 |
| GET | `/user/me` | 当前登录人 |
| GET | `/warehouse/list?keyword=&page=1&size=10` | 仓库分页查询 |
| GET | `/warehouse/options` | 仓库下拉列表 |
| GET | `/warehouse/{id}` | 仓库详情 |
| POST | `/warehouse/save` | 新增仓库 |
| PUT | `/warehouse/{id}` | 修改仓库 |
| DELETE | `/warehouse/delete/{id}` | 删除仓库（有入库记录时拒绝） |
| GET | `/product/list?keyword=&page=1&size=10` | 商品分页查询 |
| GET | `/product/options` | 商品下拉列表 |
| GET | `/product/{id}` | 商品详情 |
| POST | `/product/save` | 新增商品 |
| PUT | `/product/{id}` | 修改商品 |
| DELETE | `/product/delete/{id}` | 删除商品（连带清理入库记录） |
| GET | `/inbound/list?keyword=&page=1&size=10` | 入库记录分页查询 |
| POST | `/inbound/save` | 新增入库 `{productId, warehouseId, quantity}` |
| DELETE | `/inbound/{id}` | 撤销入库（扣回库存） |

返回体示例：

```json
{ "code": 200, "message": "success", "data": { "list": [], "total": 0, "page": 1, "size": 10 } }
```

错误码约定：`200` 成功、`400` 参数校验失败、`401` 未登录/凭据错误、`404` 资源不存在、`500` 业务或系统错误。

## 六、几个设计说明（面试会被问）

**1. 为什么入库要单独写一个 `InboundRecord`，不直接改库存？**
入库是「事件」，库存是「状态」。事件要留痕（谁、哪批、多少、什么时候），状态由事件累加得出。直接改库存会丢掉审计信息，出问题无法回溯。

**2. 入库时记录和库存怎么保证一致？**
`InboundRecordService#create` 上标注 `@Transactional`：先查商品与仓库是否存在（不存在抛业务异常），再 `stock += quantity` 保存商品，最后保存记录。任一步失败整体回滚，不会出现「记录写了但库存没加」。
撤销入库同理：先校验 `当前库存 >= 本次入库数量`，否则拒绝（避免负库存），然后扣减并删除记录。

**3. JWT 鉴权流程**
登录 → `JwtUtil.generate()` 用 HMAC-SHA 签名，subject 存 userId、claim 存 username → 前端把 token 存 localStorage，axios 请求拦截器统一加 `Authorization` 头 → 后端 `AuthInterceptor` 解析校验，成功则把 userId/username 放进 request attribute，失败直接返回 HTTP 401 + `{code:401}`。前端响应拦截器遇到 401 清空本地 token 并跳转登录页。

**4. 为什么不直接用 Spring Security 全家桶？**
这个项目的权限模型只有「登录 / 未登录」两种角色，引入 Spring Security 的过滤器链和 `SecurityContext` 收益不大。只用了它的 `spring-security-crypto` 做 BCrypt，鉴权用 20 行的 `HandlerInterceptor` 实现，代码量小、可读性好。如果后续要做 RBAC（仓库管理员只能看自己的仓库），再迁移到 filter chain 更合适。

**5. 分页与搜索怎么做的？**
统一用 `PageResult<T>{list,total,page,size}` 包装 `Page`。前端页码从 1 开始，服务层转成 Spring Data 的 0 基。关键字搜索用 `@Query` + `like`，商品搜 SKU/名称/分类，仓库搜编码/名称/地址/管理员，入库搜商品名/仓库名。`size` 上限 100，避免被刷大页。

**6. 密码怎么存的？**
BCrypt 哈希，`@JsonProperty(access = WRITE_ONLY)` 保证序列化时不输出，登录响应只返回 `UserVO`（id/用户名/昵称/手机号）。历史明文密码在启动时由 `DataInitializer` 就地升级，不需要手工改库。

**7. 金额为什么用 `BigDecimal`？**
`Double` 做金额会有 `0.1 + 0.2 != 0.3` 的精度问题，数据库里用 `DECIMAL(10,2)`，Java 里用 `BigDecimal`，校验 `@DecimalMin("0.00")`。

## 七、本次重构改了什么

| 级别 | 问题 | 处理 |
| --- | --- | --- |
| P0 | `InboundRecordRepository.findByProductId()` 字段名写错（实体里是 `product`），Spring Data 初始化时抛 `PropertyReferenceException`，**服务无法启动** | 改为 `findByProduct_Id`，并补 `@Query` 删除方法 |
| P1 | 密码明文存储、登录响应把整个 `User`（含密码）返回给前端 | BCrypt + `WRITE_ONLY` + `UserVO` 脱敏 |
| P1 | 没有任何服务端鉴权，前端路由守卫可被绕过 | JWT + `AuthInterceptor` |
| P1 | 前端请求拦截器读 `localStorage.token`，但登录只写了 `user`，token 永远为空（死代码） | 统一存 `token`，登录后自动携带 |
| P1 | 数据库密码硬编码在 `application.properties` | 改为 `${DB_PASSWORD:}` 环境变量注入 |
| P2 | 入库不更新商品库存 | `stock += quantity`，与记录同事务 |
| P2 | 删除商品时遍历删入库记录但没有事务 | `@Transactional` + `@Modifying` 批量删 |
| P2 | 全表 `findAll()`，没有分页和搜索 | `PageResult` + `Pageable` + `@Query` |
| P2 | 入库没有数量校验，可以入库 0 或负数 | `InboundRequest` + `@Min(1)` |
| P3 | 全局异常处理只捕获 `BindException`（`@RequestBody` 校验实际抛 `MethodArgumentNotValidException`），且 `getFieldError()` 可能 NPE | 同时处理两种，安全取首条字段错误 |
| P3 | 异常直接 `Result.fail(e.getMessage())`，把 SQL/堆栈暴露给前端 | 业务异常返回明确文案，未知异常记录日志 + 兜底提示 |
| P3 | 残留 `mybatis-spring-boot-starter-test` 依赖（项目用的是 JPA） | 移除 |
| P3 | `LOGIN_ERROR(401)` 定义了但从不生效（抛 `RuntimeException` 走 500 分支） | 统一 `BusinessException` 携带 code |

## 八、已知取舍与后续可做

- 单机单体，没有分布式事务/缓存；数据量小（课程项目规模），够用。
- 没有做 RBAC 与操作日志，若要上线需要补「谁改了库存」的审计表。
- 入库只加不减扣之外的业务（如出库、调拨、盘点）尚未实现，可复用现有事件表模型扩展。
- 前端没有做单元测试，只有接口层的 H2 集成测试。
