# clever-nashorn
使用Java nashorn Js引擎开发的在线开发平台，使用场景主要有：
1. 监听MySQL binlog事件，使用js动态脚本处理数据变更逻辑(MySQL数据实时同步到Elasticsearch 、Redis、MongoDB、MQ、其他任何数据库)
1. 数据集成类似ETL系统
1. 多系统数据同步平台
1. 在线接口功能开发(二次开发平台)
1. 其他所有需要动态脚本逻辑开发的系统...

### 在线体验地址

[http://nashorn.msvc.top/workbench/index.html](http://nashorn.msvc.top/workbench/index.html "http://nashorn.msvc.top/workbench/index.html")


### 特性介绍

1. 代码保存就能生效，不需要重启服务器
1. 在线调试方便，所见即所得
1. 动态监听(调试)线上的js脚本，快速定位脚本问题(bug)
1. Js生态 + Java生态，Java、Js代码库能混合使用(调用)
1. Web IDE不需要安装开发环境，随时随地开发
1. 高性能，与原生Java一致的性能
1. 可扩展性强，新增自定义扩展简单方便

### 系统截图

![001](https://raw.githubusercontent.com/Lzw2016/clever-nashorn/master/images/001.png)
工作台


![002](https://raw.githubusercontent.com/Lzw2016/clever-nashorn/master/images/002.png)
智能提示


![003](https://raw.githubusercontent.com/Lzw2016/clever-nashorn/master/images/003.png)
方法签名提示

### TODO

- [x] 支持Elasticsearch的各种操作
- [x] 整合[alibaba/canal](https://github.com/alibaba/canal "alibaba/canal")支持解析MySql binlog触发执行动态Js脚本同步数据或者业务逻辑(**重点功能**)
- [ ] 支持主流的MQ API(**重点功能**)
- [ ] 编写操作文档和手册(**重点功能**)
- [ ] 完善内置工具API支持，尽量做到只写业务逻辑代码

### 后续版本计划
- [ ] 升级Js引擎到graaljs支持ES6或者更新的Js语法
- [ ] 引入GraalVM支持Java、Python、Ruby、R、Scala、Kotlin，甚至是C、C++语言的混用
- [ ] 升级Web IDE基于[Eclipse Theia](https://theia-ide.org/ "Eclipse Theia")做二次开发，IDE功能更加强大，交互更加友好

### 感谢JetBrains Open Source license(s)

![intellij-idea](https://raw.githubusercontent.com/Lzw2016/clever-nashorn/master/images/icon-intellij-idea.png)
[JetBrains Open Source license(s)] (https://www.jetbrains.com/?from=clever-nashorn)
