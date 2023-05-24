# 零、什么是组件化
为了避免一些小伙伴一脸懵的进来，又一脸懵的出去，我先简单的说一下什么是组件化。

开发程序时，我们都希望功能间的耦合度尽可能的低，这样的好处是：便于并行开发、代码易于维护、出问题时也好定位。

所以降低功能间的耦合度就是我们开发项目时的一个需求。

具体到安卓开发当中，我们可以对一个具体的功能模块建立一个Module，然后该功能的需求全都在该Module内实现。最后在最上层建立一个Application Module，打包的时候就让他依赖需要的功能就好了。

这样的实现同样会引入问题：**Module间的调用问题**，因为在结构上来说Module都是同级的且谁也不依赖谁，同样的谁也看不到谁的方法。

这就是组件化要解决的核心问题，就是Module间功能调用的问题。

当然标准答案应该是：
1. Module间四大组件的调用。
2. Module间的方法功能调用。

但是在AppJoint的设计思想中，将以上两个需求合并成为了一个需求。

明确了需求之后我们在需要做的就是开发一个可以实现该功能的框架。这样的框架一般来说都叫路由框架，这里十分推荐大家看一下 `ARouter` 的实现原理，基本上现有路由框架的实现思路和使用的技术技巧都可以在里面看到影子。

接着我们说一下路由框架的一般实现方式：

 1. 当前Module对其他Module开放一个接口，该接口有当前Modules所有可对外开放的方法。
 2. 当前Module内部实现该接口，并实现对外开放方法；同时使用带有路径参数的注解标记Activity、Fragment 方便外部的调用。
 3. 路由框架完成接口与接口实现的映射关系；同时完成路径与Activity、Fragment之间的映射关系。
  这一步是各个路由框架主要需要提供的功能。
 4. 其他组件，通过路由框架，传入接口，调用对外开放的方法；传入路径启动Activity或者获得Fragment。

这里再说一下为什么调用四大组件也是可以归并为方法调用。

首先组件间是不知道彼此有什么Activity的，但是Module内部是知道；所以我们可以在接口的实现方法中开放启动Activity的能力：

```kotlin
    override fun startActivityOfApp(context: Context) {
        val intent = Intent(context, AppActivity::class.java)
        context.startActivity(intent)
    }
```

那么在不知道Activity的类时，也只需要传入上下文就可以启动该Activity了。
获取Fragment和上面的方式类似，给方法加一个返回值就可以了。

所以至此，我们仅需要对外开放接口，就可以解决组件化引入的问题。
# 一、[AppJoint2](https://github.com/JustGank/AppJoint2)的由来
最开始接触组件化应该是在2020年的时候，那个时候进行多种的技术选型，主要考虑的维度就是尽可能引入一个轻量的框架，对现有程序做最小的修改。

基于这个考虑我们使用了 [AppJoint](https://github.com/PrototypeZ/AppJoint)  ，这个框架非常的轻量，不看插件部分的话的核心类100行左右，注解也只有三个。

随着时间的推移，原作者不再维护该库，而Gradle不断的升级，这导致出现了下面的问题：

![在这里插入图片描述](https://img-blog.csdnimg.cn/b40e541825064a58b1cb43407aebcde6.png)
这个问题我也复现分析了一下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2bdc9e709bf444e08538572aaede5b10.png)
红框内的内容为编译时插件在做 `Transform` 时读取编译好的Jar包名称。这里之前Gradle版本读取的是正确的Jar Name ； 而新版的Gradle读取的并非是字符串意义上的Jar Name，这就导致依赖Jar Name的逻辑出错，进一步导致框架运行报错。

报错代码为：

```groovy
if (appJointClassFile == null) {
      throw new RuntimeException("AppJoint class file not found, please check \"io.github.prototypez:app-joint-core:{latest_version}\" is in your dependency graph.")
}

```

因为找不到目标Jar包，所以也找不到要注入的类，此时框架就会报错，编译终止。

所以为了解决以上的问题，同时由于`Transform` 从 Gradle 8+ 开始删除了该API，也就是说插件部分需要做较大修改，所以才有了该库。

# 二、优化内容

 1. Gradle版本升级适配
	原版使用：AGP 3.4.2 + Gradle 5.1.1
    适配至： AGP 8.0.1 + Gradle 8.1.1 
    截止到发稿日期 Gradle 最新版本为 8.1.1。
	
	不再使用Transform（Gradle 8+开始删除了该API），更改为Task实现。
	目前插件模块不存在Deprecated的方法。

 2. 将原版：
    Java(Core)+Kotlin(Demo)+Groovy(Plugin)  切换为 
    Kotlin(Core)+Kotlin(Demo)+Kotlin(Plugin) 
    即全部切换为Kotlin实现
	![在这里插入图片描述](https://img-blog.csdnimg.cn/32afce4174084aa8a8ec08907655c5ad.png)
由于切换Kotlin带来的问题已经全部适配解决。 
 3. 优化掉和框架无关的信息，整体突出AppJoint2。
 4. 优化项目结构：
 
| 框架组件名称 | 功能 |
|--|--|
| appjoint2-annotation | 声明项目中所有涉及的注解 |
| appjoint2-core  | 路由的内核 |
| appjoint2-gradle-plugin  | 生成路由代码的插件 |

| Demo组件名称 | 功能 |
|--|--|
| app | 组件化最上层的壳 |
| module-A  | 展示组件化能力的 module-A |
| module-B  | 展示组件化能力的 module-B |
| module_common  | 组件的共有依赖 |


5.优化现有的数据结构，当前的业务需求可以使用更简单的数据结构。优化完毕后内核只有一个AppJoint2一个类。

6.增加日志模块配置参数，可以更灵活的查看编译日志；为不同等级的日志配置颜色，方便查看插件编译日志。

![在这里插入图片描述](https://img-blog.csdnimg.cn/7d94df06fadb4d069eec13c6e403387c.png)
蓝色为Debug、绿色为Info、黄色为Warning。
# 三、引入AppJoint2
 
 1. 添加  jitpack.io
 项目级 `settings.gradle`  添加:
```groovy
pluginManagement {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
dependencyResolutionManagement {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

2. 添加插件地址：
项目级 `build.gradle` 添加:
```groovy
buildscript {
    dependencies {
        classpath "com.github.JustGank:AppJoint2-Plugin:1.0.3"
    }
}
```

3. 引入插件
Application Module 添加 `'com.appjoint2.plugin'`：

```groovy
plugins {
    id 'com.android.application'
    //添加插件
    id 'com.appjoint2.plugin'
}
```
4. 添加依赖
可以放到底层Module，方便上层参与打包的Module都可以访问到。
```groovy
dependencies {
	api 'com.github.JustGank:AppJoint2-Class:1.0.0'
}
```
# 四、如何使用 
## 4.1 标记主Application

```kotlin
@AppSpec
class AppApplication : Application()
```

## 4.2 标记对外开放服务的实现

```kotlin
@ServiceProvider
class ModuleAServiceImpl : ModuleAService
```

由于接口可以有多实现，所以这里也支持自定义实现的别名如：

```kotlin
@ServiceProvider("another")
class AppServiceImpl2 : AppService
```
注：如果使用了别名，那么调用Service的时候也要传入对应的别名。

## 4.3 标记子Module的Application

```kotlin
@ModuleSpec(priority = 2)
class ModuleAApplication : Application()
```
此处支持优先级，优先级值越低，越先被加载，他们的生命周期和主Application同步。

## 4.4 调用对外开放服务的方法

```kotlin
AppJoint2.service(ModuleBService::class.java)?.startActivityOfModuleB(context)
```
## 4.5 控制编译日志

引入插件后，可以在主Application Module 的 `build.gradle` 中最外层声明：
```kotlin
AppJoint2LogConfig  {
    openLog true
    debug  false
    outputTime false
}
```
 - openLog:是否输出编译日志。
 - debug:是否输出debug日志，输出的日志更为详细，方便调试模式场景下查看日志。
 - outputTime:是否输出格式化时间。

# 五、增量编译
由于使用新的Task方式实现，经测试目前直接**支持增量编译**，之前一直存在的不支持增量编译的问题已经不再存在。 

# 六、源码以及Demo
AppJoint的源码以及路径：
[https://github.com/PrototypeZ/AppJoint](https://github.com/PrototypeZ/AppJoint)

AppJoint2:
[https://github.com/JustGank/AppJoint2](https://github.com/JustGank/AppJoint2)
# 七、参考-致谢
整个AppJoint2的开发并不是一件太简单的事，这个过程中做了很多的的学习、思考与实践。
这里面给出涉及的参考并致谢：

1. 扩展 Android Gradle 插件
[https://developer.android.google.cn/studio/build/extend-agp?hl=zh_cn](https://developer.android.google.cn/studio/build/extend-agp?hl=zh_cn)

2. 你的插件想适配Transform Action? 可能还早了点
[https://juejin.cn/post/7190196880469393463](https://juejin.cn/post/7190196880469393463)

3. Transform API 废弃了，路由插件怎么办？
[https://juejin.cn/post/7222091234100330554](https://juejin.cn/post/7222091234100330554)

4. 为什么说TransformAction不是Transform的替代品
[https://juejin.cn/post/7218847310309064741](https://juejin.cn/post/7218847310309064741)

5. Gradle User Manual        
[https://docs.gradle.org/current/userguide/userguide.html](https://docs.gradle.org/current/userguide/userguide.html)

