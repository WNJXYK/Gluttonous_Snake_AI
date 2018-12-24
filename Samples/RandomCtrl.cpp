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
#define RBEGIN(x) (x[x.size()-1])
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
bool mp[MAXN][MAXN];
int from[MAXN][MAXN], dist[MAXN][MAXN];
queue<PII> que;
inline void bfs(PII src){
	int x0=src.fi, y0=src.se;
	memset(dist, -1, sizeof(dist)); while(!que.empty()) que.pop();
	from[x0][y0]=-1; dist[x0][y0]=0, que.push(MP(x0, y0));
	while(!que.empty()){
		int x=que.front().fi, y=que.front().se; que.pop();
		for (int _k=0; _k<4; _k++){
			int k=ik[_k];
			int nx=x+dx[k], ny=y+dy[k];
			if (nx<1 || nx>N || ny<1 || ny>N) continue;
			if (mp[nx][ny] || dist[nx][ny]>=0) continue;
			from[nx][ny]=k, dist[nx][ny]=dist[x][y]+1, que.push(MP(nx, ny));
		}
	}
}

// Generate Map With Snake and Food
inline void render2Map(vector<PII> snake, PII food, bool cTail = false, bool cFood = false){
	memset(mp, false, sizeof(mp));
	int len=snake.size();
	for (int i=0; i<len-(cTail?0:1); i++) mp[snake[i].fi][snake[i].se]=true;
	if (cFood) mp[food.fi][food.se]=true;
}

// Simulate Operations
vector<PII> simSnake;
static deque<int> simOps;
inline bool simulate(){
	static deque<PII> mSnake; while(!mSnake.empty()) mSnake.pop_front();
	// Copy
	for (int i=0, l=snake.size(); i<l; i++) mSnake.PB(snake[i]);
	while(!simOps.empty()){
		// Get Operations
		int op=simOps.front(); simOps.pop_front();
		// Move
		int nx=mSnake.front().fi+dx[op], ny=mSnake.front().se+dy[op];
		mSnake.push_front(MP(nx, ny));
		if (nx==food.fi && ny==food.se) {
			// Eat Food
		}else mSnake.pop_back();
		// Check Alive
		if (nx<1 || ny<1 || nx>N || ny>N) return false;
		for (int i=1, l=mSnake.size(); i<l; i++) if (mSnake[0]==mSnake[i]) return false;
	}
 	// Return
	simSnake.clear();
	while(!mSnake.empty()) simSnake.PB(mSnake.front()), mSnake.pop_front();
	return true;
}


// Get Cnts
void dfs(int x, int y){
	mp[x][y]=true;
	for (int k=0; k<4; k++){
		int nx=x+dx[k], ny=y+dy[k];
		if (nx<1 || nx>N || ny<1 || ny>N) continue;
		if (mp[nx][ny]==false) dfs(nx, ny);
	}
}
int getCnts(){
	int ret=0;
	for (int i=1; i<=N; i++){
		for (int j=1; j<=N; j++){
			if (mp[i][j]==false){
				++ret; dfs(i ,j);
			}
		}
	}
	return ret;
}

// Get Dists
int getDist(PII food, vector<PII> snake){
	int ret=0;
	for (int i=0, l=snake.size(); i<l; i++) ret+=fabs(snake[i].fi-food.fi)+fabs(snake[i].se-food.se);
	return ret;

}

struct AnswerNode{
	int k;
	int dFood, dTail;
	int cnts, dists;
}ans[5];
int siz;

bool switchFlag, winFlag;
inline bool cmp(const AnswerNode &a, const AnswerNode &b){
	if (winFlag){
		if (a.dFood<0 && b.dFood>=0) return false;
		return true;
	}
	if (a.dTail<0 && b.dTail>=0) return false;
	if (a.dTail>=0 && b.dTail<0) return true;
	if (a.dFood<0 && b.dFood>=0) return false;
	if (a.dFood>=0 && b.dFood<0) return true;
	if (switchFlag){
		if (a.cnts!=b.cnts) return a.cnts<b.cnts;
		if (rand()%10==0){
			if (a.dists!=b.dists) return a.dists>b.dists;
		}else if (a.dFood!=b.dFood) return a.dFood<b.dFood;
	}else{
		if (rand()%10==0){
			if (a.dists!=b.dists) return a.dists>b.dists;
		}else if (a.dFood!=b.dFood) return a.dFood<b.dFood;
		return a.cnts<b.cnts;
	}
}

// Init Program
inline void init(){
	// Random Seed
	srand(time(0));
	// Move Table
	for (int i=0, l=rand()%100+1; i<l; i++) random_shuffle(ik, ik+4);
	cerr<<"MT: "; for (int i=0; i<4; i++) cerr<<ik[i]<<" "; cerr<<endl;
	// SwtichFlag
	winFlag=switchFlag=false;
}

// Input Data
inline void input(){
	scanf("%d%d", &N, &K);
	for (int i=0, x, y; i<K; i++) scanf("%d%d", &x, &y), snake.PB(MP(x, y));
	scanf("%d%d", &food.fi, &food.se);
}


int main(){
	// freopen("input.txt", "r", stdin);
	init(); input();

	if (N*N-snake.size()<N*N/10) switchFlag=true;
	if (N*N-snake.size()==1) { cerr<<"Gonna Win!"<<endl; winFlag=true; }
	render2Map(snake, food, true, false);
	if (N*N-snake.size()==2 && getCnts()==1) { cerr<<"GGGonna Win!"<<endl; winFlag=true; }

	for (int _k=0, k; _k<4; _k++){
		k=ik[_k];
		while(!simOps.empty()) simOps.pop_back(); simOps.PB(k);
		if (!simulate()) continue;
		cerr<<"Available: "<<k<<endl;
		ans[++siz].k=k;
		render2Map(simSnake, food, false, true), bfs(simSnake[0]);
		ans[siz].dTail=dist[RBEGIN(simSnake).fi][RBEGIN(simSnake).se];
		render2Map(simSnake, food, false, false), bfs(simSnake[0]);
		ans[siz].dFood=dist[food.fi][food.se];
		render2Map(simSnake, food, true, rand()%2);
		cerr<<"Tail:"<<ans[siz].dTail<<" Food:"<<ans[siz].dFood<<endl;
		ans[siz].cnts=getCnts();
		ans[siz].dists=getDist(food, simSnake);
	}
	if (siz==0) return printf("%d\n", rand()%4)*0; 
	
	sort(ans+1, ans+siz+1, cmp);
	printf("%d\n", ans[1].k);

	return 0;
}
