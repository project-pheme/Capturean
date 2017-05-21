Test
====


Issues
------

Upgrading hbase:
sometimes after upgrading to the newer hbase version, you have to clean the zookeeper data
in order to remove old regionserver data.
To do it, ensure that the zookeeper is running, and run:
```
./bin/hbase clean --cleanZk
```

