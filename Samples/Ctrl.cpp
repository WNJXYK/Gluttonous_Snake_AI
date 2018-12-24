#include <cstdio>
#include <cstring>
#include <algorithm>
#include <iostream>
#include <cmath>

#include <cstdlib>
#include <ctime>

#include <vector>
#include <queue>
#include <stack>
#include <set>

#define PII pair<int, int>
#define PB(x) push_back(x)
#define MP(x, y) make_pair(x, y)
#define fi first
#define se second
using namespace std;

const double EPS=0.5;
const int INF=1048576;
const int MAXN=50;
const int dx[] = {-1, 1, 0, 0};
const int dy[] = {0, 0, -1, 1};

// Information
int N, K;
vector<PII> snake;
PII food;

int ik[]={0, 1, 2, 3};

// Get Path
bool vis[MAXN][MAXN], mp[MAXN][MAXN];
int from[MAXN][MAXN];
queue<PII> que;
inline void bfs(PII src){
	int x0=src.fi, y0=src.se;
	memset(vis, false, sizeof(vis));
	while(!que.empty()) que.pop();
	
	from[x0][y0]=-1; vis[x0][y0]=true, que.push(MP(x0, y0));
	while(!que.empty()){
		int x=que.front().fi, y=que.front().se; que.pop();
		for (int _k=0; _k<4; _k++){
			int k=ik[_k];
			int nx=x+dx[k], ny=y+dy[k];
			if (nx<1 || nx>N || ny<1 || ny>N) continue;
			if (mp[nx][ny]) continue;
			if (vis[nx][ny]) continue;
			from[nx][ny]=k, vis[nx][ny]=true, que.push(MP(nx, ny));
		}
	}
}

// Exvalue For Free Block
const int FREEBLOCK_LIMIT=10, FREEBLOCK_RAND=10;
int EXTRA_VALUE, EXTRA_LEN;
inline int extraValue(int len){
	if (EXTRA_LEN==0) return len*EXTRA_VALUE*INF;
	if (len==EXTRA_LEN) return EXTRA_VALUE;
	return 0;
}

// Get Path EX
bool inQue[MAXN][MAXN];
double dist[MAXN][MAXN], value[MAXN][MAXN];
int fromEx[MAXN][MAXN];
set<PII> path[MAXN][MAXN];
inline void spfa(PII src){
	int x0=src.fi, y0=src.se;
	memset(inQue, false, sizeof(inQue));
	for (int i=1; i<=N; i++) for (int j=1; j<=N; j++) dist[i][j]=-INF, path[i][j].clear();
	while(!que.empty()) que.pop();
	
	que.push(MP(x0, y0));
	fromEx[x0][y0]=-1;
	dist[x0][y0]=value[x0][y0];
	path[x0][y0].insert(MP(x0, y0));
	inQue[x0][y0]=true;

	while(!que.empty()){
		int x=que.front().fi, y=que.front().se; que.pop(); 
		for (int _k=0; _k<4; _k++){
			int k=ik[_k];
			int nx=x+dx[k], ny=y+dy[k];
			if (nx<1 || nx>N || ny<1 || ny>N) continue;
			if (mp[nx][ny]) continue;
			if (path[x][y].count(MP(nx, ny))>0) continue;
			if (dist[x][y]+value[nx][ny]+extraValue(path[x][y].size())>dist[nx][ny]+EPS){
				dist[nx][ny]=dist[x][y]+value[nx][ny]+extraValue(path[x][y].size());
				path[nx][ny]=path[x][y];
				path[nx][ny].insert(MP(nx, ny));
				if (fromEx[x][y]==-1) fromEx[nx][ny]=k; else fromEx[nx][ny]=fromEx[x][y];
				if (!inQue[nx][ny]) inQue[nx][ny]=true, que.push(MP(nx, ny));
			}
		}
		inQue[x][y]=false;
	}
}

// Get Path Operations
stack<int> ops;
inline int getDirection(PII pos){
	if (!vis[pos.fi][pos.se]) return -1;
	int x=pos.fi, y=pos.se, ret=0;
	while(!ops.empty()) ops.pop();
	while(from[x][y]!=-1){
		ret=from[x][y];
		x-=dx[ret], y-=dy[ret];
		ops.push(ret);
	}
	return ret;
}

inline void generateMap(){
	memset(mp, false, sizeof(mp));
	int len=snake.size();
	for (int i=0; i<len-1; i++) mp[snake[i].fi][snake[i].se]=true;
}

deque<PII> mSnake;
inline void simulate(){
	while(!mSnake.empty()) mSnake.pop_front();
	for (PII x: snake) mSnake.PB(x);
	
	while(!ops.empty()){
		int op=ops.top(); ops.pop();
		int nx=mSnake.front().fi+dx[op], ny=mSnake.front().se+dy[op];
		mSnake.push_front(MP(nx, ny));
		if (!ops.empty()) mSnake.pop_back();
	}
}

inline void input(){
	scanf("%d%d", &N, &K);
	for (int i=0, x, y; i<K; i++) scanf("%d%d", &x, &y), snake.PB(MP(x, y));
	scanf("%d%d", &food.fi, &food.se);
}

int main(){
	// freopen("input.txt", "r", stdin);
	input();

	// Free Block Bonus
	srand(time(0));
	EXTRA_LEN=rand()%FREEBLOCK_RAND+3; EXTRA_VALUE=0;
	if (N*N-snake.size()<max(FREEBLOCK_LIMIT, N*N/10)){ // Special Ending Bonus
		EXTRA_LEN=0;
		cerr<<"As Long as Possible"<<endl;
	}else cerr<<"I want "<<EXTRA_LEN<<"Free Blocks"<<endl;

	// Random Move Table
	for (int i=0, l=rand()%100+1; i<l; i++) random_shuffle(ik, ik+4);
	for (int i=0; i<4; i++) cerr<<ik[i]<<" "; cerr<<endl;

	// Eat Food
	generateMap();
	bfs(snake[0]);
	int foodD=getDirection(food);
	cerr<<"Towards Food: "<<foodD<<endl;
	if (snake.size()==N*N-1 && foodD!=-1) {
		cerr<<"I Gonna Win!"<<endl;
		return printf("%d\n", foodD)*0;
	}

	// Follow Tail
	generateMap();
	int fx=food.fi, fy=food.se, gx=snake[K-1].fi, gy=snake[K-1].se, tailD;
	mp[fx][fy]=true;
	double left=0, right=N*2, mx=0;
	while(left+EPS<right){
		double mid=(left+right)/2;
		for (int i=1; i<=N; i++) for (int j=1; j<=N; j++) value[i][j]=fabs(i-fx)+fabs(j-fy)-mid;
		spfa(snake[0]);
		if (dist[gx][gy]>EPS){
			mx=max(mx, mid);
			left=mid+EPS;
		}else right=mid-EPS;
	}
	cerr<<"AFF Max: "<<mx<<endl;
	if (mx>EPS){
		for (int i=1; i<=N; i++) for (int j=1; j<=N; j++) value[i][j]=fabs(i-fx)+fabs(j-fy)-mx;
		EXTRA_VALUE=5; // Set Gonna For Free Block
		spfa(snake[0]);
		tailD=fromEx[gx][gy];
		cerr<<"To Tail Length: "<<path[gx][gy].size()<<endl;
	}else tailD=-1;
	cerr<<"Towards Tail: "<<tailD<<endl;

	// No Food Then Follow the Tail
	if (foodD<0) return printf("%d\n", tailD)*0;

	// Eat Food And Then Follow Tail
	generateMap(), bfs(snake[0]), getDirection(food);
	simulate();
	snake.clear(); while(!mSnake.empty()) snake.PB(mSnake.front()), mSnake.pop_front();
	generateMap(); //printMAP();
	bfs(snake[0]);
	int checkSafe=getDirection(snake[K]);
	if (checkSafe<0) printf("%d\n", tailD); else printf("%d\n", foodD);
	cerr<<"Eat Food Safe? "<<checkSafe<<endl;


	return 0;
}
