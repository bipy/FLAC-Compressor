# 基于 FLAC 的多线程压缩脚本

在 [mora](https://mora.jp/) 等在线音乐商店购买的 Hi-Res 音源往往过大，这主要是由于没有经过压缩导致的，使用 FLAC 进行**无损压缩**可以显著降低音乐体积。

FLAC 提供了 1 ~ 8 八种压缩等级，其中 Level 8 对应”压缩最慢，体积最小“，因此选择 Level 8 作为压缩参数。（这里讨论的是无损压缩，用时间和算力换体积，对音质无任何影响）

# Feature

- 解决 flac 串行处理效率太低的问题，使用线程池并行计算
- 递归访问
- 计数回显

**脚本非常简陋，无完善纠错机制和拓展性，但是正常使用能完成提高效率的目的。**



# 原理

使用 Java 线程池调用 `flac` 递归访问所选文件夹下所有 flac 文件进行无损压缩。

调用参数如下：

```shell
flac -e -p -f -8 -s test.flac
```



# 使用方法

修改 `Main.java`  中相关变量：

-  `flac` 路径 `FLAC_PATH`
-  待处理路径 `INPUT_PATH`
-  线程数 `THREAD_COUNT` 推荐设置为 `2 * core - 1`

运行：

```shell
javac -encoding UTF-8 Main.java
java Main
```



# Demo

![](https://cdn.jsdelivr.net/gh/bipy/CDN@master/repo/FLAC-Compressor/cover.jpg)

**Object:** [RADWIMPS - 愛にできることはまだあるかい](https://mora.jp/package/43000006/00602508485718/) 

**Detail:** 24bit/48.0kHz, FLAC, Hi-Res

### Before

**Size:** 118,533 KB (116.4 MB)

**SoX:**

![](https://cdn.jsdelivr.net/gh/bipy/CDN@master/repo/FLAC-Compressor/before.png)

### After

**Size:** 81,983 KB (80.1 MB)

**SoX:**

![](https://cdn.jsdelivr.net/gh/bipy/CDN@master/repo/FLAC-Compressor/after.png)

