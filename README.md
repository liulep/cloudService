# 微服务

## 配置

|服务|描述|
|--|--|
|authorization-filter|下游服务用户信息解析配置|
|authorization-server	|认证服务器配置|
|mybatis-plus|mybatis配置|
|alibaba-nacos|nacos配置|
|redis-cache|redis配置|
|resource-server|网关层面的资源服务器配置|
|resp-return|消息返回配置|
|route-dynamic|动态路由配置|
|route-register|自动注册服务路由|
|open-feign|远程服务Http调用|

## 业务服务

|服务|端口|描述|
|--|--|--|
|auth-service|8081|登录认证服务|
|gateway-service|8080|网关路由服务|
|user-service|8082|用户服务|
|order-service|8084|订单服务|
|product-service|8083|商品服务|

## 技术选型

- 持久层框架：MyBatis、MyBatis-Plus
- 微服务框架：SpringCloud Alibaba
- 消息中间件：RocketMQ
- 服务治理与服务配置：Nacos
- 负载均衡组件：Ribbon
- 远程服务调用：OpenFegin
- 服务限流与容错：Sentinel
- 服务网关：SpringCloud-Gateway
- 服务链路追踪：Sleuth+ZipKin
- 分布式事务：Seata
- 数据存储：MySQL+ElasticSearch
- 服务认证授权：SpringSecurity+OAuth2
- 缓存数据库：Redis

## 编码日志

> 2023-05-21以前
> 
> 未进行编码说明

> 2023-05-22
> 
> 自定义认证服务的返回格式
> 
> ```json
> 原先格式
> {
>     "access_token": "eyJjbGllbnQtaWQiOiJ0ZXN0IiwiYWxnIjoiUlMyNTYiLCJraWQiOiJjYjZkYjdkZi1jNzc1LTRiZjEtYjhlNy1kZDMzYjI4ZmE4ZjAifQ.eyJzdWIiOiJhZG1pbiIsImF1ZCI6InRlc3QiLCJuYmYiOjE2ODQ3NjEwMzcsInVzZXJfaWQiOjEsInNjb3BlIjpbImFkZHJlc3MiLCJvcGVuaWQiLCJwcm9maWxlIiwiZW1haWwiXSwiaXNzIjoiaHR0cDovLzEyNy4wLjAuMTo4MDgxL2lzc3Vlci95dWV1ZSIsIm5pY2tuYW1lIjoi566h55CG5ZGYIiwiZXhwIjoxNjg0ODA0MjM3LCJpYXQiOjE2ODQ3NjEwMzcsImF1dGhvcml0aWVzIjpbeyJyb2xlIjoiUk9MRV9BRE1JTiJ9LHsicm9sZSI6IlJPTEVfVVNFUiJ9XSwidXNlcm5hbWUiOiJhZG1pbiJ9.Yu3-vO048e9qS6Bbnp6uDQkvnhwQpgdsstgGNHFK1OBDQVVPC6W7crkVCR3619iJbxDUaXBI-f7G1io5bb2ZEXYhH9J_GftwlhlOsulQX6efKu-1E9CJHX7lCEnuv8NTEJM8LjiMvlo71BlwZDZkMsgF0O-J1oW-NPpbkYxCEcTdfMwcx_3B9p6FCvq2sfJ6nEhsX6fFcE_JTfGu2Tt43BFpmOJGsPDgLhIta9ij3GFLEKbuA3KUiCHV2-kLWSc6FNV_IZ7tY-eYaA-BdGVyvCtFddepeO3-G7A_fm1yJoRt4qiaagy1jFEOf2r7ECrQdHI6b6qWyOKA26H7QpD5_A",
>     "refresh_token": "upkrzfLV9ghDCrAUNPYj3lpyp4w4Ldrd25bXy0tye4uPeoZBzuGkscpL3GkVYYs-FsR_5gzL3V30399V0ptQexDRIzoFoDtCG7y72MDhOJCux26IdFsau10Sgkk9vQ5f",
>     "scope": "address openid profile email",
>     "id_token": "eyJjbGllbnQtaWQiOiJ0ZXN0IiwiYWxnIjoiUlMyNTYiLCJraWQiOiJjYjZkYjdkZi1jNzc1LTRiZjEtYjhlNy1kZDMzYjI4ZmE4ZjAifQ.eyJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjgwODEvaXNzdWVyL3l1ZXVlIiwic3ViIjoiYWRtaW4iLCJhdWQiOiJ0ZXN0IiwiZXhwIjoxNjg0NzYyODM4LCJpYXQiOjE2ODQ3NjEwMzgsImF6cCI6InRlc3QifQ.Yk2y2Jl7B5JOUfUZ6depr9xtOskQtPEyeOfu40l4widEXTIwItnWUUCyP7Xf5wFVP7gFJ2KDkWKkhvbNgr_d-nCW8jv42cfjzAkGYxc7KWvP9K4O3fTbPomhccFzXyoV1tK08XrPOeCyiT1IGCy_mYYwr_uuDae5mcWyq4cf-F5GEhcnL_NjcWct73Glco9ZxREKnaSKR8MZKq05aANmLJxu0CKzC-iciWWVclITpUH8-zlp6LSiFABrUYUfGCjeTT0lRbGxzWl38GZzzyKz0sIpylKuwoNPrKXd8GcQkc0aKqT2M3eTt2v4WCuafNe61VJYwRznCqTyLLqozmnMzw",
>     "token_type": "Bearer",
>     "expires_in": 43199
> }
> 自定义格式
> {
>     "code": 200,
>     "data": {
>         "access_token": "eyJjbGllbnQtaWQiOiJ0ZXN0IiwiYWxnIjoiUlMyNTYiLCJraWQiOiI0ZjdlZDRhOS1jYzFhLTRhNmEtOWQ5Yi0xZTA1MWVhN2Q3ZDUifQ.eyJzdWIiOiJhZG1pbiIsImF1ZCI6InRlc3QiLCJuYmYiOjE2ODQ3NjY5NjAsInVzZXJfaWQiOjEsInNjb3BlIjpbImFkZHJlc3MiLCJvcGVuaWQiLCJwcm9maWxlIiwiZW1haWwiXSwiaXNzIjoiaHR0cDovLzEyNy4wLjAuMTo4MDgxL2lzc3Vlci95dWV1ZSIsIm5pY2tuYW1lIjoi566h55CG5ZGYIiwiZXhwIjoxNjg0ODEwMTYwLCJpYXQiOjE2ODQ3NjY5NjAsImF1dGhvcml0aWVzIjpbeyJyb2xlIjoiUk9MRV9BRE1JTiJ9LHsicm9sZSI6IlJPTEVfVVNFUiJ9XSwidXNlcm5hbWUiOiJhZG1pbiJ9.SeeIoMmV1Rm0SOddlJZqMbkG8EGWnLafn73f8qVBUnao1ZJs6F1rrL9Oxq7xd4lfU4XJDcCQVxurR2TjmkVrLfbSzk4MMly1qs8aF1nsp_aeVv4jSB53gUN97QToFJMfrAExVnquMfNbwM1uDVOlVI8f_Z6gEbl-uCXVFy2Nndhh0CRMd1LLI7X6NFM5ck-L0GKvhzjErs-tgU_tkKaEZnoDMKI2-1aNc5v1w21mWYQH17tbLr7KS1e3acMmpRnXynQt8wmGu3He_Bb5_RxbyHjmeC3zWiW0Y6Ea7EdGtCI6KFxTKqkwQa-OayMQBEvJrKkaVHEnwPN_j2_PZusWSg",
>         "token_type": "Bearer",
>         "refresh_token": "0P8i0nR1AjxjpdBDGMa9OzlJWUKyVYpceBb-W6XJtkY2Q0AvueiKnz--bAUMyUQ4BuNMo_0hhgQo75iJxlkQkzyD_9oSwFuYUVoRWTSUKZS1Ar6rwU0dphu-OlROJxE_",
>         "expires_in": 43200
>     },
>     "message": "成功",
>     "success": true
> }
> ```

> 2023-05-23
> 
> 修复网关资源服务引入redisTemplate提示未找到错误
> 
> 更正nacos配置从数据库中读取
> 
> 修复nacos权限错误无法进行动态读取
> 
> 增加NacosConstant常量类

> 2023-05-24
> 
> 修复无法从网关跳转至认证服务
> 
> 修改dev本地环境配置
> 
> 增加prod正式环境配置

> 2023-05-25
> 
> 添加雪花算法
> 
> 新增用户服务 => userService
> 
> 新增商品服务 => productService
> 
> 新增订单服务 => orderService

> 2023-05-27
> 
> 解决资源服务器JwtDecoder非阻塞问题

> 2023-05-28
> 
> 增加open-feign starter
> 
> 新增alibaba-sentinel starter
> 
> 网关服务整合sentinel,并nacos持久化sentinel配置信息
> 
> 各个服务接入sentinel

> 2023-05-29
> 
> 更新项目结构
> 
> 各个服务接入openfeign

> 2023-05-30
> 
> 添加禁止重复提交事件

> 2023-06-02
> 
> 添加服务下线后清除缓存防止5XX
