# 微服务

## 配置

|服务|描述|
|--|--|
|authorization-filter|下游服务用户信息解析|
|authorization-server	|认证服务器|
|mybatis-plus|mybatis配置|
|alibaba-nacos|nacos配置|
|redis-cache|redis配置|
|resource-server|资源服务器|
|resp-return|消息返回|
|route-dynamic|动态路由|
|route-register|自动注册服务路由|
|open-feign|远程服务Http调用|
|file-storage|文件上传|

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
> 修复网关资源服务引入redisTemplate提示未找到
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
> 配置open-feign
> 
> 配置alibaba-sentinel
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

> 2023-06-03
> 
> 使用ReactiveRedis作为SpringCloudGateway路由缓存(引入redis依赖，路由缓存自动切换到redis中)

> 2023-06-08
>
> 添加自定义认证方式(OAuth2PhonePasswordAuthorization)：手机号码+密码方式 grant_type:phone,username:手机号,password:密码
>
> 配置Amazon S3(网上流行的对象存储服务比如MInio,阿里云OSS,腾讯云COS都提供了对Amazon S3协议访问处理的API)

> 2023-06-09
>
> 创建file-service

> 2023-06-10
>
> 增加禁止重复操作
>
> ```java
> String key = String.format("%s:uid_%d%s#%d", KeyConstant.AVOID_REPEATABLE_COMMIT,hashcode);
> //key = yue-service:uid_21317676212329743123
> //设置过期时间,采用setNx互斥操作
> Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, SnowFlakeFactory.getSnowFlake().nextId(), timeout, TimeUnit.MILLISECONDS);
> //如果已经存在
> if(Boolean.FALSE.equals(flag)){
>   ...
> }
> ```
>
> 

> 2023-06-19
>
> 添加文件切片上传
>
> 切片上传实现原理
>
> 1. 文件分割：将待上传的大文件分割成固定大小的小片段（切片）。通常，每个切片的大小可以根据具体需求进行配置，例如每个切片的大小为1MB或者其他合适的大小。
>
> 2. 切片编号和元数据：为了管理切片的顺序和信息，每个切片需要分配一个唯一的编号，并维护一些元数据信息，例如文件名称、总切片数等。这些信息可以存储在客户端的内存中或者与服务器进行交互来保存。
>
> 3. 切片上传：开始逐个上传切片。客户端将每个切片分别上传到服务器端，可以使用HTTP协议或者其他文件传输协议进行传输。每个切片都带有相关的元数据，以便服务器能够识别和重组切片。
>
> 4. 切片接收和存储：服务器端接收到切片后，根据切片的元数据信息进行存储。可以将切片暂时存储在内存中或者写入临时文件中，以便后续的切片组装。
>
> 5. 切片校验和重传：在上传过程中，服务器端可以对每个切片进行校验和验证，确保切片的完整性和正确性。如果某个切片上传失败或损坏，客户端可以重新上传该切片，直到所有切片都被成功上传。
>
> 6. 切片组装：当所有切片都上传完成后，服务器端根据切片的编号和元数据信息对切片进行组装，恢复原始的完整文件。组装可以在服务器端完成，也可以将切片传输回客户端由客户端进行组装。
>
> 7. 完整性校验和确认：在文件组装完成后，可以对整个文件进行完整性校验，以确保文件的正确性。客户端和服务器端可以进行比对，验证文件是否完整无误。
>
>    暂时通过同步分切片上传
