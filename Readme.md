Artemis（Ctrip SOA 服务注册表）
================

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Artemis是携程框架部门SOA 服务注册表，包含服务自注册自发现、实例变更实时推送、服务分组路由功能。

更多产品介绍参见: https://github.com/ctripcorp/artemis/wiki

# Features
* 服务自注册自发现

* 实例变更实时推送

* 实例拉入拉出管理

* 分组路由


# Deployment
Steps
1. Create a artemisdb by sql in: artemis-web/deployment/artemis-management
2. Config the data-source.properties file
3. User artemis-web/deployment/server.xml to replace your tomcat server.xml

Once you deploy artemis service, you can visit swagger page to see its api doc. 
eg. http://localhost:8080/artemis-web/swagger-ui.html

# Developers
* Qiang Zhao <koqizhao@outllook.com>

* Alex <fangjing828@gmail.com>

* jianwj <i-jianwj@outlook.com>

* wanbf <513111602@qq.com>

* Jodie <qianjin1120@126.com>

* Dante <383124397@qq.com>

