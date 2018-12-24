#include <cstdio>
#include <cstring>
#include <algorithm>
#include <iostream>
#include <vector>
#include <queue>
#include <stack>
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

// Information
int N, K;
vector<PII> snake;
PII food;

// Get Path
bool vis[MAXN][MAXN], mp[MAXN][MAXN];
int from[MAXN][MAXN];
queue<PII> que;
inline void bfs(PII src){
	int x0=src.fi, y0=src.se;
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
	//freopen("in.txt", "r", stdin);
	input();

	// Eat Food
	generateMap();
	bfs(snake[0]);
	int foodD=getDirection(food);

	// Follow Tail
	generateMap();
	mp[food.fi][food.se]=true;
	bfs(snake[0]);
	int tailD=getDirection(snake[K-1]);

	// No Food Then Follow the Tail
	if (foodD<0) return printf("%d\n", tailD)*0;
	
	// Debug
	cerr<<foodD<<" "<<tailD<<" "<<endl;

	// Eat Food And Then Follow Tail
	generateMap();
	bfs(snake[0]);
	getDirection(food);
	simulate();
	snake.clear();
	while(!mSnake.empty()) snake.PB(mSnake.front()), mSnake.pop_front();
	generateMap(), bfs(snake[0]);
	int checkSafe=getDirection(snake[K]); cerr<<checkSafe<<endl;
	if (checkSafe<0) printf("%d\n", tailD); else printf("%d\n", foodD);

	return 0;
}
