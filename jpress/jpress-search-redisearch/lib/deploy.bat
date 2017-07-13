start /b
mvn install:install-file -Dfile=jedis-3.0.0.jar -DgroupId=redis.clients -DartifactId=jedis -Dversion=3.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=jredisearch-1.0-SNAPSHOT.jar -DgroupId=redisearch.clients -DartifactId=jredisearch -Dversion=1.0-SNAPSHOT -Dpackaging=jar
