# shift-jis-garbling

Shift-JIS を利用して発生する文字化けを確認するためのサンプル

## 環境構築

Minikube 上に OpenLiberty と Db2 を導入する。

### Minikube の設定

```bash
minikube start --vm-driver=hyperkit
eval $(minikube docker-env)
```

### Db2 のイメージ取得

dockerhub の以下のイメージを入手する。
https://hub.docker.com/_/db2-developer-c-edition

docker-credential-osxkeychain を求められたら以下を導入する。
https://github.com/docker/docker-credential-helpers/releases

```bash
docker login
docker pull store/ibmcorp/db2_developer_c:11.1.4.4-x86_64
```

env_list という名前で以下の内容のファイルを作っておく

```bash
LICENSE=accept
DB2INSTANCE=db2inst1
DB2INST1_PASSWORD=password
DBNAME=testdb
BLU=false
ENABLE_ORACLE_COMPATIBILITY=false
UPDATEAVAIL=NO
TO_CREATE_SAMPLEDB=false
REPODB=false
IS_OSXFS=false
PERSISTENT_HOME=true
HADR_ENABLED=false
ETCD_ENDPOINT=
ETCD_USERNAME=
ETCD_PASSWORD=
```

```bash
docker run -h db2server \
--name db2server --restart=always \
--detach \
--privileged=true \
-p 50000:50000 -p 55000:55000 \
--env-file env_list
store/ibmcorp/db2_developer_c:11.1.4.4-x86_64
```

```bash
docker run --name db2server --env-file env_list --privileged=true -p 50000:50000 -p 55000:55000 --rm -it  store/ibmcorp/db2_developer_c:11.1.4.4-x86_64 bash
```

このイメージは codeset が utf-8 で固定されている。

/var/db2_setup/include/db2_common_functions

```bash
db2 create db ${dbname?} using codeset utf-8 territory us collate using identity
```

ここでは

```bash
su - db2inst1
db2 create db sjisdb using codeset ibm-943 territory jp collate using identity
```

root で
localedef -f SHIFT_JIS -i ja_JP /usr/lib/locale/ja_JP.sjis
https://t-horikawa.hatenablog.com/entry/20161014/1476421579

```bash
docker cp 5930c936be94:/database/config/db2inst1/sqllib/java/db2jcc4.jar .
```

## わかったこと

- HTML が Shift_JIS の場合、画面に Shift_JIS 範囲外の文字（😄 など）を入力するとブラウザが自動的に数値文字参照(16 進数)へ変換する。サーバーサイドは数値文字参照のまま処理されるので DB のコードセットは考慮不要になる。同じ文字が再度ブラウザに送られると、ブラウザが画面表示時に実体参照へ変換する。
- HTML が UTF-8 の場合、ブラウザは実体参照のままサーバーへ送信する。サーバーサイドでは DB のコードセットに応じて変換を行うが INSERT は実行されてしまう。しかし SELECT のタイミングで CharConversionException(MalformedInputException) が発生する。
- cent sign(U+00A2)のような文字も同じ扱いとなっているように見えるがこちらはレスポンスで消えてしまう。

```Java
// スタックトレースの最後の部分のみ抜粋
Caused by: com.ibm.db2.jcc.am.SqlException: [jcc][t4][1065][12306][4.25.13] java.io.CharConversionException をキャッチしました。 詳しくは、添付の Throwable を参照してください。 ERRORCODE=-4220, SQLSTATE=null
at com.ibm.db2.jcc.am.b6.a(b6.java:794)
at com.ibm.db2.jcc.am.b6.a(b6.java:66)
at com.ibm.db2.jcc.am.b6.a(b6.java:125)
at com.ibm.db2.jcc.am.bh.a(bh.java:2963)
at com.ibm.db2.jcc.am.bh.p(bh.java:575)
at com.ibm.db2.jcc.am.bh.ab(bh.java:2835)
at com.ibm.db2.jcc.am.ResultSet.getObjectX(ResultSet.java:1493)
at com.ibm.db2.jcc.am.ResultSet.getObject(ResultSet.java:1464)
at com.ibm.ws.rsadapter.jdbc.WSJdbcResultSet.getObject(WSJdbcResultSet.java:1403)
at [internal classes]
at org.eclipse.persistence.internal.databaseaccess.DatabasePlatform.getObjectFromResultSet(DatabasePlatform.java:1424)
at org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor.getObject(DatabaseAccessor.java:1308)
... 42 more
Caused by: java.nio.charset.MalformedInputException: Input length = 1
at java.nio.charset.CoderResult.throwException(CoderResult.java:274)
at com.ibm.db2.jcc.am.x.a(x.java:52)
at com.ibm.db2.jcc.am.bh.a(bh.java:2952)
... 50 more
```

- `-Ddb2.jcc.charsetDecoderEncoder=3`というオプションを付与すると、REPLACEMENT CHARACTER(\uFFFD�)へ自動変換される
- https://www-01.ibm.com/support/docview.wss?uid=swg22005262
- https://www-01.ibm.com/support/docview.wss?uid=swg21973226

数値文字参照を使うには`&#x2015;`のように入力すると良い

TODOあとで確認。方向と最後の３つ

https://graphemica.com/

| 文字説明   | 片方向                              | 両方向                                         |
| ---------- | ----------------------------------- | ---------------------------------------------- |
| ダッシュ   | U+2015                              | U+2014                                         |
| 波ダッシュ | U+FF5E                              | U+301C                                         |
| 双柱       | U+2225                              | U+2016                                         |
| マイナス   | U+FF0D                              | U+2212                                         |
| 破断線     | U+FFE4                              | U+00A6                                         |
| セント     | U+FFE0 (fullwidth cent sign) 残る   | U+00A2 (cent sign) 消える                      |
| ポンド     | U+FFE1 (fullwidth pound sign)  残る | U+00A3 (pound sign) 消える                     |
| 否定記号   | U+FFE2 (fullwidth not sign)  残る   | U+00AC (not sign) 消える、JIS X 0213に含まれる |

円がバックスラッシュになるとかもあるかも