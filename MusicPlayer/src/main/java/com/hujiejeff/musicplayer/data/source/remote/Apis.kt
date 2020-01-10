package com.hujiejeff.musicplayer.data.source.remote

import com.hujiejeff.musicplayer.data.entity.PlayListCatlistResponse
import com.hujiejeff.musicplayer.data.entity.PlayListDetailResponse
import com.hujiejeff.musicplayer.data.entity.PlayListsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Create by hujie on 2020/1/8
 */

public const val baseUrl = "http://localhost:3000"

interface Apis {

    /**
     * ------------歌单
     * */

    /**
     * 获取歌单分类 test ok
     * */
    @GET("playlist/catlist")
    fun getPlayListCatList(): Call<PlayListCatlistResponse>

    /**
     * 获取热门歌单分类
     * */
    @GET("playlist/hot")
    fun getHotPlayListCatList()

    /**
     * 获取普通歌单
     * 可选参数 : order: 可选值为 'new' 和 'hot', 分别对应最新和最热 , 默认为 'hot'
     * cat:cat: tag, 比如 " 华语 "、" 古风 " 、" 欧美 "、" 流行 ", 默认为 "全部",可从歌单分类接口获取(/playlist/catlist)
     * test OK
     * */
    @GET("top/playlist")
    fun getNormalPlayLists(
        @Query("cat") cat: String = "全部",
        @Query("limit") limit: Int = 20,
        @Query("order") order: String = "hot"
    ): Call<PlayListsResponse>

    /**
     * 获取精品歌单
     * 可选参数 : cat: tag, 比如 " 华语 "、" 古风 " 、" 欧美 "、" 流行 ", 默认为 "全部",可从歌单分类接口获取
     * limit: 取出歌单数量 , 默认为 20, before: 分页参数,取上一页最后一个歌单的 updateTime 获取下一页数据
     * */
    @GET("/top/playlist/highquality")
    fun getHighQualityPlayLists(@Query("before") before: Int, @Query("limit") limit: Int)

    /**
     * 获取歌单详情，包含歌曲列表信息
     * 必选参数 : id : 歌单 id
     * 可选参数 : s : 歌单最近的 s 个收藏者
     * test ok
     * */
    @GET("playlist/detail")
    fun getPlayListDetail(@Query("id") id: Long): Call<PlayListDetailResponse>


    /**
     * ---------------音乐
     * */


    /**
     * 获取音乐URL
     * 必选参数 : id : 音乐 id
     * 可选参数 : br: 码率,默认设置了 999000 即最大码率,如果要 320k 则可设置为 320000,其他类推
     * */
    @GET("song/url")
    fun getMusicUrl(@Query("id") id: Int, @Query("br") br: Int)

    /**
     * 检测音乐是否可用
     * 说明: 调用此接口,传入歌曲 id, 可获取音乐是否可用,返回 { success: true, message: 'ok' } 或者 { success: false, message: '亲爱的,暂无版权' }
     * 必选参数 : id : 歌曲 id
     * 可选参数 : br: 码率,默认设置了 999000 即最大码率,如果要 320k 则可设置为 320000,其他类推
     * */
    @GET("check/music")
    fun checkMusic(@Query("id") id: Int)

    /**
     * 搜索,
     * 说明 : 调用此接口 , 传入搜索关键词可以搜索该音乐 / 专辑 / 歌手 / 歌单 / 用户 , 关键词可以多个 , 以空格隔开 ,
     * 如 " 周杰伦 搁浅 "( 不需要登录 ), 搜索获取的 mp3url 不能直接用 , 可通过 /song/url 接口传入歌曲 id 获取具体的播放链接
     * */
    @GET("search")
    fun search(@Query("keywords") keywords: String)

    /**
     * 获取默认搜索关键字
     * */
    @GET("search/default")
    fun getDefaultSearch()

    /**
     *  获取热搜榜
     * */
    @GET("search/hot")
    fun getHotSerach()

    /**
     *  搜索建议
     * */
    @GET("search/suggest")
    fun getSearchSuggest(@Query("keywords") keywords: String, @Query("type") type: String = "mobile")


}