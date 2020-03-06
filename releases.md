### xxxx-xx-xx 1.0.1.RELEASES

- [bug fix]

- [增强]

- [新增]

- [变化]

---

### 2019-11-11 0.0.1-SNAPSHOT

##### [发布第一个版本]
  
  - WEB IDE 功能
    - 脚本文件管理(新删查改)
    - 脚本内容编辑(代码高亮、只能提示、快捷键支持)
    - 控制台(终端)管理(清除输出、根据日志级别筛选过滤、跟随滚动)
    - 脚本在线debug，即改即生效
    - 直接监听线上脚本调用输出
  
  - 后台API支持
    - 脚本模块化支持(相互依赖导入，类似ES6语法)
    - 支持加载使用第三方库，如：lodash、Underscore、等等
    - HTTP API支持
    - JDBC API支持 (支持多数据源)
    - Redis API支持 (支持连接多个Redis)
    - Elasticsearch API支持 (支持连接多个Elasticsearch)
    - 支持实时监听解析MySQL binlog(基于canal实现，重写部分canal源码) (支持连接多个MySQL master)

