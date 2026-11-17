这是一个奶茶点单系统
一共有五张表 包括：员工表 奶茶表 会员表 会员订单表 订单明细表

事务涉及drink member teaorder orderitem表
整个流程使用 JDBC 事务，任何一步失败则全部回滚，不会留下半截订单。

如何验证事务生效
1.临时修改库存减扣的条件 使其必然失败（改成 WHERE stock >= 99999）
2.执行一次正常下单
3. 登录数据库查看：
   - SELECT * FROM tea_order ORDER BY id DESC LIMIT 3;
   -SELECT * FROM order_item ORDER BY id DESC LIMIT 3;
   - SELECT * FROM drink WHERE id = ?;
4. 预期：订单表没有新增记录，库存数字不变
5. 把条件改回原样，再下一次单，确认恢复正常

> ⚠️ 第 5 步别忘，否则忘了改回来，之后所有下单都会“莫名失败”。

环境要求：
JDK 21（与 pom.xml 的编译目标一致；若只有 JDK 17，需把 pom.xml 里的 21 改成 17）
maven 3.9.16 
mysql 8.0.46

数据库脚本位于项目根目录的 sql/init.sql，包含建库、建表、初始数据三部分。
数据库名：milktea_saga
命令行:mysql -u root -p < sql/init.sql(在项目根目录下)
执行完后 用一下命令确认：
SHOW DATABASES LIKE 'milktea_saga'; 看到库名即可


使用sql： mysql -u root -p123456 --default-character-set=utf8mb4
USE milktea_saga;

USE milktea_saga;
SHOW TABLES;    应列出五张表
预期结果：tea_order / order_item / drink / member / ...（你的 5 张表名）

init.sql 末尾已包含初始数据：5 种奶茶（含库存）、2 位会员（王同学积分 280、李学姐积分 1024）。
因此建库后可直接运行程序，无需手动录入。

用户名，密码写在init.sql文件里

> 脚本开头已包含 DROP DATABASE IF EXISTS milktea_saga;，
> 可重复执行（每次执行会清空原有数据，请勿在有重要数据时运行）。

数据库地址在dbutil.java文件


## 怎么跑 

| 事项 | 内容 |
|---|---|
| 入口类 | `com.tea.Main`（文件位置：`src/main/java/com/tea/Main.java`，即 `main` 方法所在类） |
| 编译命令 | `mvn clean compile` |
| 运行命令 | 见下方「方式二」 |

### 方式一：IDE 运行（推荐）

打开 `src/main/java/com/tea/Main.java`，点 `main` 方法上方的 ▶ 运行按钮，自动完成编译和运行。

### 方式二：命令行运行（PowerShell，在项目根目录执行）

```powershell
mvn clean compile
mvn dependency:build-classpath "-Dmdep.outputFile=cp.txt"
java -cp "target\classes;$(Get-Content cp.txt)" "com.tea.Main"
```

- 第 1 条：编译，出现 `BUILD SUCCESS` 即成功
- 第 2 条：生成依赖路径文件 `cp.txt`（只跑一次即可，依赖没改动可跳过）
- 第 3 条：启动程序，`-cp` 里必须包含 MySQL 驱动，否则登录时会报「找不到驱动」

### 注意：PowerShell 参数里的点号

带点号的参数必须用双引号包起来，否则 PowerShell 会把它拆成两半：

| 错误写法 | 报错 | 正确写法 |
|---|---|---|
| `-Dmdep.outputFile=cp.txt` | Unknown lifecycle phase ".outputFile=cp.txt" | `"-Dmdep.outputFile=cp.txt"` |
| `com.tea.Main` | 找不到或无法加载主类 com.tea | `"com.tea.Main"` |

### 版本说明

`pom.xml` 里的 `maven.compiler.target` 目前是 21，所以编译需要 JDK 21。
如果本机只有 JDK 17，请把 `pom.xml` 中 `maven.compiler.source` 和 `maven.compiler.target` 都改成 17，再重新执行 `mvn clean compile`，否则运行时会报 `UnsupportedClassVersionError`。