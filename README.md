# hss_es_jd
es_示例

## 依照视频教程搭建
1.RestHighLevelClient高级客户端使用（操作索引：增、删，操作文档：增删改查，批量操作）

2.使用jsoup爬取网页数据，并存入es索引中

3.设置字段类型（title字段支持ik分词）
PUT jd_goods
{
  "mappings": {
    "properties": {
      "title": {
      "type": "text",
      "analyzer": "ik_max_word",
      "search_analyzer": "ik_smart"
      },
      "img":{
        "type": "text"
      },
      "price":{
        "type": "text"
      }
    }
  }
}

4.提供查询接口，使用vue快速搭建页面，实现功能（搜索框）