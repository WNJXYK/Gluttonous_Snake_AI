# 贪吃蛇自动寻路模拟系统

本想写一个可以吃到100%的AI贪吃蛇程序，然后写着写着就造了一个轮子：支持键盘/程序控制的可变速、可调试、可视化程序。



## 支持功能

* 可视化贪吃蛇界面
* 统计步数与完成率
* 选择可执行程序/键盘控制贪吃蛇
* 即时改变速度&暂停
* 可视化界面上显示程序调试信息



## 动图预览 (35.5MB)

!(界面预览)[https://github.com/WNJXYK/Gluttonous_Snake_AI/blob/master/Images/RandCtrl.gif]



## 目录结构

* [Bin](https://github.com/WNJXYK/Gluttonous_Snake_AI/tree/master/Bin) 存放可执行的Jar文件
* [Samples](https://github.com/WNJXYK/Gluttonous_Snake_AI/tree/master/Samples) 存放了三个示例代码：`Sample.cpp`,`RandCtrl.cpp`,`Ctrl.cpp`
* [Source_Code](https://github.com/WNJXYK/Gluttonous_Snake_AI/tree/master/Source_Code) 存放Java程序的源代码
* [Statements](https://github.com/WNJXYK/Gluttonous_Snake_AI/tree/master/Statements) 存放详细的英文使用说明与交互解释

## 交互规定

程序输入从`stdin`取得数据，输出到`stdout`，调试信息输出到`stderr`。

需要注意的是，输出数据应该以新行结束。对于C/C++选手来说，就应该使用`printf("%d\n", ans);`或者`cout<<ans<<endl;`来输出数据。

输入数据的第一行有两个整数n与k，分别表示游戏地图的边长与贪吃蛇的长度。

接下来![](http://latex.codecogs.com/gif.latex?i)行，每行有两个整数![](http://latex.codecogs.com/gif.latex?x_i)与![](http://latex.codecogs.com/gif.latex?y_i)，表示贪吃蛇从头到尾第![](http://latex.codecogs.com/gif.latex?i)节的位置。

最后一行，有两个整数![](http://latex.codecogs.com/gif.latex?x_{food})与![](http://latex.codecogs.com/gif.latex?y_{food})表示食物的位置。

输出数据只有一个整数![](http://latex.codecogs.com/gif.latex?d\in[0,4))，表示贪吃蛇移动的方向。

贪吃蛇移动的方向0~4分别映射WSAD四个方向，如下代码可以帮助你来理解。

```java
public static final int dx[] = {-1, 1, 0, 0};
public static final int dy[] = {0, 0, -1, 1};
```

#### 输入格式 (stdin)

![](http://latex.codecogs.com/gif.latex?n~k)

![](http://latex.codecogs.com/gif.latex?x_1~y_1)

![](http://latex.codecogs.com/gif.latex?\cdots)

![](http://latex.codecogs.com/gif.latex?x_k~y_k)

![](http://latex.codecogs.com/gif.latex?x_{food}~y_{food})

#### 输出格式 (stdout)
![](http://latex.codecogs.com/gif.latex?d)



## Debug贴士

当你使用`程序控制模式`的时候，程序会在根目录生成一个`input.txt`，它的内容与输出到可执行控制程序内容一致的。你可以使用这个数据进行离线查错，而不用特别构造数据或者将图形化界面的情况转化为数据。

当你的控制程序向`stderr`输出信息的时候，会显示在程序右下角的`Controller Recoder`中。



## 示例控制代码

```cpp
#include <cstdio>
#include <cstring>
#include <algorithm>
#include <iostream>
#include <vector>
#include <queue>
#include <cstdlib>
#include <ctime>
#define PII pair<int, int>
#define PB(x) push_back(x)
#define MP(x, y) make_pair(x, y)
#define fi first
#define se second
using namespace std;

const int MAXN=50;
const int dx[] = {-1, 1, 0, 0};
const int dy[] = {0, 0, -1, 1};

int N, K;
bool mp[MAXN][MAXN];
vector<PII> snake;
PII food;

bool vis[MAXN][MAXN];
int from[MAXN][MAXN];
queue<PII> que;
inline void bfs(int x0, int y0){
	memset(vis, false, sizeof(vis));
	while(!que.empty()) que.pop();
	
	from[x0][y0]=-1, vis[x0][y0]=true, que.push(MP(x0, y0));
	while(!que.empty()){
		int x=que.front().fi, y=que.front().se; que.pop();
		for (int k=0; k<4; k++){
			int nx=x+dx[k], ny=y+dy[k];
			if (nx<1 || nx>N || ny<1 || ny>N) continue;
			if (mp[nx][ny]) continue;
			if (vis[nx][ny]) continue;
			from[nx][ny]=k, vis[nx][ny]=true, que.push(MP(nx, ny));
		}
	}
}

inline int getDirection(PII pos){
	if (!vis[pos.fi][pos.se]) return -1;
	int x=pos.fi, y=pos.se, ret=0;
	while(from[x][y]!=-1){
		ret=from[x][y];
		x-=dx[ret], y-=dy[ret];
	}
	return ret;
}


inline void init(){
	memset(mp, false, sizeof(mp));
}

inline void input(){
	scanf("%d%d", &N, &K);
	for (int i=0, x, y; i<K; i++) scanf("%d%d", &x, &y), snake.PB(MP(x, y));
	scanf("%d%d", &food.fi, &food.se);
}

int main(){
	init(), input();
	
	srand((unsigned)time(NULL));
	
	for (int i=0; i<K; i++) mp[snake[i].fi][snake[i].se]=true;
	mp[snake[K-1].fi][snake[K-1].se]=false;
	bfs(snake[0].fi, snake[0].se);
	int foodD=getDirection(food), tailD=getDirection(snake[K-1]);
	if (foodD>=0 && tailD>=0) printf("%d\n", foodD); else{
		if (tailD>=0) printf("%d\n", tailD); else{
			printf("%d\n", rand()%4);
		}
	}
	
	return 0;
}
```



## 其他注意

* 控制程序目录不支持存在空格
* 本程序需要在有Java Runtime Environment的环境下运行