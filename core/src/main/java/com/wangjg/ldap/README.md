### （一）目录树概念

1. 目录树：再一个牧鞭服务系统中，整个目录信息集可以表示为一个目录信息树，<font color='red'>树中的每个节点是一个条目</font>。
2. <font color='red'>条目：每条条目就是一条记录</font>，每个条目有自己的唯一可区别的名称（DN）
3. 对象类：与某个实体类型对应的一组属性，<font color='red'>对象类是可以继承的</font>。这样父类的必须属性也会呗继承下来。
4. 属性：描述条目的某个方面的信息，<font color='red'>一个属性有一个属性类型和一个或多个属性值组成</font>，属性又必须属性和非必需属性。

### （二）DC、UID、OU、CN、SN、DN、RDN

| 关键字 | 英文全称           | 含义                                                         |
| :----: | :----------------- | :----------------------------------------------------------- |
|   dc   | Domain Componet    | 域名的部分，其格式是将完整的域名分成几部分，如域名为 example.com 变成 dc=example,dc=com（一条记录的所属位置） |
|  uid   | User Id            | 用户ID songtao.xu（一条记录的ID）                            |
|   ou   | Organization Unit  | 组织单位，组织单位可以包含其他各种对象（包括其他组织单元），如“oa组”（一条记录的所属组织） |
|   cn   | Common Name        | 公共名称，如“Thomas Johansson”                               |
|   sn   | Surname            | 姓“许”                                                       |
|   dn   | Distinguished Name | "uid=songtao.xu,ou=oa组,dc=example,dc=com"，一条记录的位置（唯一） |
|  rdn   | Relative dn        | 相对辨别名，类似于文件系统中的相对路径，他是与目录树结构无关的部分，如“uid=tom” 或 “cn=Thomas Johansson” |

### （三）基本模型

- 信息模型：在 Ldap 中信息以树状方式方式组织，在树状信息中的基本数据单元是条目，而每个条目由属性构成，属性中存储有属性值；

- 命名模型：在 Ldap 中的命名模型，也就是 Ldap 中条目定位方式。在 Ldap 中每个条目均有自己的 DN。DN 是该条目在整个树中的唯一名称标识，，如同文件系统中，带路径的文件名就是 DN。

- 功能模型：在 Ldap 中共有四类 10 种操作：

  1. 查询类操作，如搜索、比较；
  2. 更新类操作，如添加条目、删除条目、修改条目、修改条目名；
  3. 认证类操作，如绑定、解绑定；
  4. 其他操作，如放弃和扩张操作。

  除了扩展操作，另外9种是 Ldap 的标准操作；扩展操作是 Ldap 中为了增加新的功能，提供的一种标准的扩展框架，当前已经成为 Ldap 标准的扩展操作，有修改密码和 StartTLS 扩展，在新的 RFC 标准和草案中正在增加一些新的扩展操作，不同的 Ldap 厂商也均定义了自己的扩展操作。

- 安全模型：Ldap 中的安全模型主要通过身份认证、安全通道和访问控制来实现。

### LDAP 的使用

如何访问 LDAP 的数据库服务器？

统一身份认证主要是改变原有的认证策略，是需要认证的软件都通过 Ldap 进行认证，在统一身份认证之后，用户的所有信息都存储在 AD Server 中。终端用户在需要使用公司内部服务的，都需要通过 AD 服务器的认证。

1. 连接到 Ldap 服务器
2. 绑定到 Ldap 服务器
3. 在 Ldap 服务器上执行所需的任何操作
4. 释放 Ldap 服务器的连接

### objectClass
LDAP 中，一个条目（Entry）必须包含一个对象类（objectClass）属性，且需要赋予至少一个值。每一个值将用作一条 LDAP 条目进行数据存储的模板；模板中包含了一个条目必须被赋值的属性和可选的属性。

objectClass有着严格的等级之分，最顶层是top和alias。例如，`organizationalPerson`这个objectClass就隶属于`person`，而`person`又隶属于top。

objectClass可分为以下3类：
结构型（Structural）：如`account`、`inetOrgPerson`、`person`和`organizationUnit`；
辅助型（Auxiliary）：如`extensibeObject`；
抽象型（Abstract）：如`top`，抽象型的 objectClass 不能直接使用。

每种 objectClass 有自己的数据结构，比如我们有一种叫“电话薄”的 objectClass ，肯定会内置很多属性（attributes），如姓名（uid），身份证号（uidNumber），单位名称（gid），家庭地址（homeDirectory）等，这些属性（attributes）中，有些是必填的，例如，`account`就要求 userid 是必填项，而`inetOrgPerson`则要求 cn（common name，常用名称）和 sn（surname，姓）是必填项。

objectClass可分为以下3类：
1. 结构型（Structural）：如`account`、`inetOrgPerson`、`person`和`organizationUnit`
2. 辅助型（Auxiliary）：如`extensibeObject`
3. 抽象型（Abstract）：如`top`，抽象型的 objectClass 不能直接使用

每种 objectClass 有自己的数据结构，比如我们有一种叫“电话薄”的 objectClass，肯定会内置很多属性（attributes），如姓名（uid），身份证号（uidNumber），单位名称（gid），家庭地址（homeDirectory）等，这些属性（attributes）中，有些是必填的，例如，account就要求userid是必填项，而`inetOrgPerson`则要求cn（common name,常用名称）和sn（sure name,真实名称）是必填项

`accout` 内置的attributes有：userid、description、host、localityName、organizationName、organizationalUnitName、seeAlso；

`inetOrgPerson` 内置的 attributes 有 cn、sn、description、seeAlso、telephoneNumber、userPassword、destinationIndicator、facsimileTelephoneNumber、internationaliSDNNumber、l、ou、physicalDeliveryOfficeName、postOfficeBox、postalAddress、postalCode、preferredDeliveryMethod、registeredAddress、st、street、telephoneNumber、teletexTerminalIdentifier、telexNumber、title、x121Address、audio、usinessCategory、carLicense、departmentNumber、isplayName、employeeNumber、employeeType、givenName、homePhone、homePostalAddress、initials、jpegPhoto、labeledURI、mail、manager、mobile、o、pager、photo、preferredLanguage、roomNumber、secretary、uid、userCertificate等；

由上可见，`accout`仅仅预置了几个必要且实用的属性（完成登陆验证肯定是够了），而`inetOrgPerson`内置了非常之多的属性，例如电话号码、手机号码、街道地址、邮箱号码，邮箱地址，房间号码，头像，经理，雇员号码等等。

因此，在配置LDAP时，如果仅仅是基于验证登陆的目的，建议将 objectClass 类型设置为 `accout`，而如果希望打造一个大而全的员工信息宝库，建议将objectClass设置为`inetOrgPerson`。

### LDAP Docker 方式搭建
```dockerfile
# docker-compose.yml
version: '3'
services:
  myopenldap:
    image: osixia/openldap:1.4.0
    ports:
      - "389:389"
      - "636:636"
    environment:
      - LDAP_TLS=flase
      - LDAP_ORGANISATION=xyz
      - LDAP_DOMAIN=xyz.com
      - LDAP_ADMIN_PASSWORD=123456
```