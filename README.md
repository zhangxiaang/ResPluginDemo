# ResPluginDemo
插件与宿主资源id冲突，通过修改插件apk中的resources.arcs和对应的res二进制资源文件从而解决这个冲突

应用于插件化中宿主和插件通用一套assetsManager，好处自然是减少apk的体量。


问题: 在采用插件与宿主公用一套AssetsManager的方案，总是会遇到resID冲突的问题。例如在宿主和插件中都有一个R.string.app_name字段 并且在R文件中解析出来都是id值为0x7f060012，前者value为"HostApp",后者value为"PluginApp",如果在插件中还是直接使用0x7f060012来加载"PluginApp"最终是失败的显示的依旧是"HostApp"，所以想在插件加载进来前修改插件中的resID值，解决方案如下:

resID的组成为pptteeee:pp为系统包资源id eeee段位为相对位移，即使改这里还是有冲突的可能性，所以修改pp段位是最佳选择.

在修改前需要检查下jdk的版本，如果是1.8版本需要降级到1.7来，因为需要手动把.class文件打包成dex文件，而dx工具在java8环境下总是遇到一些奇怪的问题。

其次在AS的file->projectStructure中检查IDE使用的是内置的jdk还是JAVA_HOME,如果用的是内置的话把内置的jdk版本也换下来。新版本AS中内置的都是jdk1.8版本。

在给AS的jdk降级后先build插件项目，使之生成R.java文件，然后把某些你想要修改的资源id都给改掉，例如把app_name 的资源值修改为了0x7e060012.
然后在terminal中编译R文件,javac R.java,将生成的一堆class文件全部挪到app/build/intermediates/classes/debug/packagename/ 下，覆盖原有的class文件。

接着开始转化dx文件  -> dx dx --dex --output=classes.dex android+app 的class文件
生成了第一步的classes.dex文件

接着是需要修改resoureces.arsc文件  这个步骤比较麻烦。需要了解resoureces.arsc这个资源映射表的结构才能知道在哪儿改动。看看老罗的一片文章[资源打包流程](http://blog.csdn.net/luoshengyang/article/details/8744683),然后才知道怎么读这个二进制文件...

mac环境推荐把resoureces.arsc转化成可读性比较好的格式:具体如下:

vi resources.arsc

:%!xxd

这样就转化成可读性比较能接受的格式了，接着就是找chunk类型为RES_TABLE_PACKAGE_TYPE的部分了,需要注意的是寻找string的pp段位信息的时候是要反着方向读取的，例如我要读取0090 0800的数据就在里面检索"0008 9000"了...

其中的RES_TABLE_PACKAGE_TYPE结构可以参考[神图](http://img.voidcn.com/vcimg/000/005/123/596_eb0_509.jpg)以及[这篇博文](http://www.voidcn.com/blog/beyond702/article/p-6068523.html)  我们的插件app的pp段位id就在packagename的前面，所以我们可以先去检索我们的包名，包名的每一个word都被‘.’隔开了所以需要注意检索词，最终检索图如下:![检索图](https://github.com/zhangxiaang/ResPluginDemo/blob/master/app/src/main/res/mipmap-xxhdpi/img1.png) 可以看到我的包名为c.o.m.b.o.y....注意7e00 0000这个数值也就是我们的packageID -> 7e 这个是被窝修改了的，原来默认的是7f的,然后修改完了要记得把resoureces.arsc的格式回复回来:
vi resoureces.arsc
:%!xxd -r
就准备好了第二份打包素材.

还需要做的就是把res AndroidManifest资源文件进行打包成一个apk文件。
(这里我用的是比较老的工具apkbuilder，推荐使用的是aapt,不过分成了先打包资源后添加classes.dex文件)
apkbuilder.sh plugins.apk -u -z resources.arsc  -f  ~/Desktop/classess.dex   -rf ~/Desktop/plugins/res/
这样就可以生成一个apk文件的但是此时尚未打包，缺少META-INF 文件夹无法加载运行。
在签名操作成功并生成了最终版本的plugins.apk后放到AS中打开查看他的resources.arsc内容如下:![img1](https://github.com/zhangxiaang/ResPluginDemo/blob/master/app/src/main/res/mipmap-xxhdpi/img2.png)
ps:由于整体修改了packageID所以所有的id都会被影响到，需要谨慎考虑。

最终被宿主加载了的话即使插件优先于宿主加载，使用R.string.app_name得到的结果仍为"HostApp",而对于插件中可以写一个工具每次访问插件的id时中间添加一个"0x7f__"-->"0x7e _"的操作。这样就可以比较好的解决资源冲突的问题了。




不过重点是:资源在编译的过程中产生的是多种类型资源文件的，本次试验仅仅考虑的是res/values/下的资源冲突问题，因为这里的资源直接解析到了string pool中，不产生对外部的res/引用，而比较麻烦的是类似layout/anim之类的资源，如果产生了冲突，不仅仅只是修改resources.arsc文件，在res/文件夹下同样需要做部分的修改，不过原理大概都是一样的，只不过分析各个文件的二进制文件确是比较伤脑筋和眼睛。


个人推荐采用插件/宿主资源隔离的方案，相对于对于资源包的增大，修改二进制的代价还是比较大的，而且预估问题的发生也比较难维护代价也高。

[命令集合](http://geosoft.no/development/android.html)