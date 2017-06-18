# 简记app

## TODO

1.  优化备份系统（解决可以在多个activity中完成同一个备份工作），理清数据流动方向
2.  硬件加速会导致有些自定义view闪退，之后要详细了解一下
3.  删除日记的代码太过混乱，调用太复杂，希望能改一下
4.  还有比较多的功能未完善，如字体下载的进度条，太简陋

## app架构

打算使用的是mvp架构，有单独的model，view，presenter等，但是关系分的不清，有时间会继续学习一下mvp架构。

就目前而言，使用的是baseActivity，baseView，baPresenter的方法。

代码如下：

```java
abstract class BaseActivity : android.support.v7.app.AppCompatActivity(){
    /*
    这里是一个activity使用的 数据获取presenter 与 数据回调的view
    一个presenter对应着一个view
    如果一个activity需要使用到这些，在initPresenter中初始化
     */
    protected val presenterList = ArrayList<IBasePresenter>()
    protected val viewList = ArrayList<IBaseView>()
    
    /*
    获取activity的对应的 layout 的id
     */
    abstract val contentView: Int
    
    /*
    初始化一些数据，bundle 即 getIntent().getExtras()
     */
    abstract fun initData(bundle: android.os.Bundle?)
    
    /*
    初始化一个view的操作，比如数据嵌入， 设置监听等
     */
    abstract fun initView()

    /*
    activity 入口
     */
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(contentView)
        initPresenter()
        initData(intent.extras)
        initView()
    }

    /*
    初始化 presenter 和 view
    需要在子类里面先初始化之后，再调用 super.initPresenter()
     */
    open fun initPresenter(){
        if (presenterList.size != viewList.size){
        }else {
            for (i in 0..presenterList.size - 1){
                presenterList[i].attachView(viewList[i])
            }
        }
    }

    /*
    用于 presenter 和 view 的解绑
     */
    override fun onDestroy() {
        super.onDestroy()

        for (i in presenterList){
            i.detachView()
        }
    }
}
```

```java

```

