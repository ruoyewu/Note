package com.wuruoye.note.model

import android.Manifest
import android.os.Environment
import com.wuruoye.note.R

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
object Config {
    val outDirect = Environment.getExternalStorageDirectory().absolutePath + "/com.wuruoye.note/out/"

    val fontList = arrayOf(
            R.drawable.chenjishi,
            R.drawable.guojinfang,
            R.drawable.jiangang,
            R.drawable.minikai,
            R.drawable.minixing,
            R.drawable.xingkai,
            R.drawable.xvjinglei,
            R.drawable.shaonvti,
            R.drawable.wawati,
            R.drawable.huati,
            R.drawable.xilijian,
            R.drawable.yuanti,
            R.drawable.weibei,
            R.drawable.fanti,
            R.drawable.songfan,
            R.drawable.xilifan,
            R.drawable.fanzuan
    )

    val fontNameList = arrayOf(
            "chenjishi.ttf",
            "guojinfang.ttf",
            "jiangang.ttf",
            "minikai.ttf",
            "minixing.ttf",
            "xingkai.ttf",
            "xvjinglei.ttf",
            "shaonvti.ttf",
            "wawati.ttf",
            "huati.ttf",
            "xilijian.ttf",
            "yuanti.ttf",
            "weibei.ttf",
            "fanti.ttf",
            "songfan.ttf",
            "xilifan.ttf",
            "fanzuan.ttf"
    )

    val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val paperStyle = arrayOf(
            R.color.romance,
            R.color.cosmos,
            R.color.athens_gray,
            R.color.mercury,
            R.color.sundown,
            R.color.french_lilac,
            R.color.pink_lace,
            R.color.blanched_almond,
            R.color.selago,
            R.color.sail,
            R.color.pale_cornflower_blue,
            R.color.riptide,
            R.color.anti_flash_white,
            R.color.desert_storm,
            R.color.mystic,
            R.color.surf_crest,
            R.color.half_baked,
            R.color.edgewater,
            R.color.paris_white,
            R.color.spring_wood,
            R.color.ebb,
            R.color.black_white,
            R.color.baby_pink,
            R.color.london_hue,
            R.color.double_spanish_white,
            R.color.mauvelous,
            R.color.ghost,
            R.color.turquoise_green,
            R.color.vista_white,
            R.color.beige
    )

    val weekList = arrayOf(
            "零",
            "日",
            "一",
            "二",
            "三",
            "四",
            "五",
            "六"
    )

    val yearList = arrayOf(
            "二零一三",
            "二零一四",
            "二零一五",
            "二零一六",
            "二零一七",
            "二零一八"
    )

    val numList = arrayOf(
            "零",
            "一",
            "二",
            "三",
            "四",
            "五",
            "六",
            "七",
            "八",
            "九",
            "十",
            "十一",
            "十二",
            "十三",
            "十四",
            "十五",
            "十六",
            "十七",
            "十八",
            "十九",
            "二十",
            "二十一",
            "二十二",
            "二十三",
            "二十四",
            "二十五",
            "二十六",
            "二十七",
            "二十八",
            "二十九",
            "三十",
            "三十一",
            "三十二",
            "三十三",
            "三十四",
            "三十五",
            "三十六",
            "三十七",
            "三十八",
            "三十九",
            "四十",
            "四十一",
            "四十二",
            "四十三",
            "四十四",
            "四十五",
            "四十六",
            "四十七",
            "四十八",
            "四十九",
            "五十",
            "五十一",
            "五十二",
            "五十三",
            "五十四",
            "五十五",
            "五十六",
            "五十七",
            "五十八",
            "五十九",
            "六十"
    )
}