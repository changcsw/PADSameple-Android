# PADSameple-Android

## 配置工程

### 创建资源包

1. 在项目的根目录下(跟App模块平级目录下)创建资源包文件夹，此文件夹名即为资源包名。注意：名字只能是字母开通，仅能包含字母、数字、下划线，如：`base_assets`
2. 在`base_assets`目录下创建`src/main/assets`目录，在此目录中存放我们的资源文件，此目录里可以有子目录
3. 在`base_asssets`目录下创建`build.gradle`文件并填入内容如下：

```groovy
// In the asset pack’s build.gradle file:
apply plugin: 'com.android.asset-pack'

assetPack {
    packName = "base_assets" // Directory name for the asset pack
    dynamicDelivery {
        deliveryType = "install-time" // 根据自己需求可填后面三种类型中的一种[ install-time | fast-follow | on-demand ]
    }
}
```

4. 在App(主入口)模块的 `build.gradle`中添加如下代码：

```groovy
// In the app build.gradle file:
android {
    ...
    assetPacks = [":base_assets"]
}
```

5. 在工程的根目录下有个`settings.gradle`文件，在此文件中添加自己建的 资源包如下：

```groovy
// In the settings.gradle file:
include ':app'
include ':base_assets'
```



### 使用资源包

1. 在项目的app(主入口) 模块下的 `build.gradle` 文件添加 `Google Play Core` 库，如下：

```groovy
// In your app’s build.gradle file:
...
dependencies {
    // This dependency is downloaded from the Google’s Maven repository.
    // So, make sure you also include that repository in your project's build.gradle file.
    implementation 'com.google.android.play:core:1.7.3'
    ...
}
```

2. 获取资源包中的资源

   - Install-time 类型的资源包，就跟在 App（主入口）模块中的assets目录下资源一样使用

   - Fast-follow 类型的资源包，会在应用安装完成后立即下载

   - On-demand 类型的资源包，是可以按需下载

     在使用上 fast-follow和on-demand类型的资源包需要通过下面方法找到资源包路径，进而读取资源（读取先要检查本地是否下载完成，检查过程参考https://developer.android.com/guide/playcore/asset-delivery/integrate-java）

     ```java
      AssetPackLocation assetPackPath = assetPackManager.getPackLocation(assetPack);
      String assetsFolderPath = assetPackPath.assetsPath();// 此目录就是资源包目录了
     ```

### 生成Android App Bundle

1. 通过Android Studio 的Build-->genrate Signed Bundle/APK 一步步导出生成Android App Bundle，即`.aab`文件

### 测试

1. 生成 带有测试标签的apks文件

   ```shell
   java -jar bundletool-all.jar build-apks --bundle=path/to/your/bundle.aab \
     --output=output.apks --local-testing
   ```

2. 连接设备并通过bundletool来安装apks文件

   ```shell
   java -jar bundletool.jar install-apks --apks=output.apks
   ```

3. 打开安装的 apks，查看资源是否都能正常获取到
