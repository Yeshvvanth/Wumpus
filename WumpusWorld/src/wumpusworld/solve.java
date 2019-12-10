package wumpusworld;

import java.util.ArrayList;

public class solve {
	
	
	World w;
	boolean safeworld[];
	boolean pitworld[];
	boolean stenchworld[];
	int neiStench[];
	int totalSize;
	
	
	public solve(World world) {
		w = world;
		totalSize = w.getSize() * w.getSize();
		safeworld = new boolean[totalSize];
		pitworld = new boolean[totalSize];
		stenchworld = new boolean[totalSize];
		neiStench= new int[totalSize];
		for(int i=0;i<totalSize;i++) safeworld[i]=false;
		for(int i=0;i<totalSize;i++) pitworld[i]=false;
		for(int i=0;i<totalSize;i++) stenchworld[i]=false;
		for(int i=0;i<totalSize;i++) neiStench[i]=0;
		
	}

	public void takeStep(int x, int y) {
		
		System.out.println(w.getPlayerX()+" "+w.getPlayerY());
		updateSafeList();

	}

	private void updateSafeList() {
		
		for(int i=0;i<totalSize;i++) neiStench[i]=0;
		int stenchDiscovered=0;
		
		//mark all neighbouring breeze as pit possible, and stench as wumpus possible
		for(int i=1;i<w.getSize()+1;i++) {
			for(int j=1;j<w.getSize()+1;j++) {
				
				if(w.isVisited(i, j)) {
					if(w.hasBreeze(i, j)) {
						setTrue(pitworld,i,j);
					}
					if(w.hasStench(i, j) && w.wumpusAlive()) {
						stenchDiscovered+=1;
						setTrue(stenchworld,i,j);
						setFalse(safeworld,i,j);
						setFalse(pitworld,i,j);
						System.out.println("in stench" + i + j);
						
					}
				}
				
			}
		}
		
		//if a square doesnt have both stench and breeze, nearby squares are safe
		for(int i=1;i<w.getSize()+1;i++) {
			for(int j=1;j<w.getSize()+1;j++) {
				
				if(w.isVisited(i, j)) {
					if(!w.hasBreeze(i, j) && !w.hasStench(i, j)) {
						setTrue(safeworld,i,j);
						setFalse(pitworld,i,j);
						setFalse(stenchworld,i,j);
					}
					if(!w.hasStench(i, j)) {
						setFalse(stenchworld,i,j);
					}
				}
				
			}
		}
		
		//set for all visited squares false, as we need to go to a unvisited square
		for(int i=1;i<w.getSize()+1;i++) {
			for(int j=1;j<w.getSize()+1;j++) {
				
				if(w.isVisited(i, j)) {
					safeworld[convert(i,j)]=false;
					pitworld[convert(i,j)]=false;
					stenchworld[convert(i,j)]=false;
				}
				
			}
		}
		
		
		//check in how many squares wumpus can be possibly located
		int wumpusPossibleCount =0;
		int wumpusx=-1,wumpusy=-1;
		for(int i=1;i<w.getSize()+1;i++) {
			for(int j=1;j<w.getSize()+1;j++) {
				
				if(stenchworld[convert(i,j)]) {
					wumpusPossibleCount+=1;
					wumpusx=i;
					wumpusy=j;
				}
			}
		}
		
		//if only one square, then kill the wumpus
		if(wumpusPossibleCount==1) {
			killWumpus(wumpusx,wumpusy);
			return;
		}
		
		
		//another method to eliminate wumpus, when more then one stench squares are discovered
		if(stenchDiscovered>1) {
			
			System.out.println("stench Discovered "+stenchDiscovered);
			for(int i=1;i<w.getSize()+1;i++) {
				for(int j=1;j<w.getSize()+1;j++) {
					if(w.hasStench(i, j)) {
						incrementNei(neiStench,i,j);
					}
				}
			}
			
			wumpusPossibleCount=0;
			for(int i=1;i<w.getSize()+1;i++) {
				for(int j=1;j<w.getSize()+1;j++) {
					if(neiStench[convert(i,j)]==stenchDiscovered) {
						wumpusPossibleCount+=1;
						wumpusx=i;
						wumpusy=j;
					}
				}
			}
			if(wumpusPossibleCount==1) {
				killWumpus(wumpusx,wumpusy);
				return;
			}
		}
		
		boolean moved =false;
		int min=10000;
		int x=w.getPlayerX();
		int y=w.getPlayerY();
		int movex=-1,movey=-1;
		
		//check if any safe squares are there, and move to the nearest one
		for(int i=1;i<w.getSize()+1;i++) {
			for(int j=1;j<w.getSize()+1;j++) {
				
				if(safeworld[convert(i,j)]) {
					int dist = Math.abs(i-x) + Math.abs(j-y);
					if(dist<min) {
						movex=i;
						movey=j;
						min=dist;
					}
					moved =true;
				}
				
			}
		}
		
		if(moved) {
			System.out.println("reached move player loop in safe" + movex +" "+movey);
			movePlayer(movex,movey);
			return;
		}
		
		//if no safe squares, then move to the nearest pit possible square
		for(int i=1;i<w.getSize()+1;i++) {
			for(int j=1;j<w.getSize()+1;j++) {
				
				if(pitworld[convert(i,j)]) {
					int dist = Math.abs(i-x) + Math.abs(j-y);
					if(dist<min) {
						movex=i;
						movey=j;
						System.out.println("in pit min check "+dist+" "+i+" "+j );
						min=dist;
					}
					moved =true;
				}
			}
			
		}
		
		if(moved) {
			System.out.println("reached move player loop in pit" + movex +" "+movey);
			movePlayer(movex,movey);
			return;
		}
		
		
		//finally only stench nearby squares are the possible moves, we move to any of the squares
		for(int i=1;i<w.getSize()+1;i++) {
			for(int j=1;j<w.getSize()+1;j++) {
				
				if(stenchworld[convert(j,i)]) {
					System.out.println("reached move player loop in stench" + i +" "+j);
					movePlayer(j,i);
					moved =true;
					return;
				}
			}
			
		}
		
		}
		
	private void incrementNei(int[] arr,int x, int y) {
		
		if(x>1) {
			if(!w.isVisited(x-1, y)) {
				int k = (x-2) * w.getSize() + y -1;
				arr[k]+=1;
				System.out.println(k+" is incremented");
			}
		}
		if(y>1) {
			if(!w.isVisited(x, y-1)) {
				int k = (x-1) * w.getSize() + y -2;
				arr[k]+=1;
				System.out.println(k+" is incremented");
			}
		}
		if(x<4) {
			if(!w.isVisited(x+1, y)) {
				int k = (x)*w.getSize() + y-1;
				arr[k]+=1;
				System.out.println(k+" is incremented");
			}
		}
		if(y<4) {
			if(!w.isVisited(x, y+1)) {
				int k = (x-1)*w.getSize() + y;
				arr[k]+=1;
				System.out.println(k+" is incremented");
			}
		}
		
	}

	private void killWumpus(int x, int y) {
		System.out.println("in kill wumpus "+x+" "+y);
		if(x>1) {
			if(w.isVisited(x-1, y)) {
				System.out.println("killing wumpus from "+(x-1)+" "+y);
				movePlayer(x-1,y);
				turnPlayer(1);
				w.doAction(World.A_SHOOT);
				return;
			}
		}
		if(y>1) {
			if(w.isVisited(x, y-1)) {
				movePlayer(x,y-1);
				turnPlayer(0);
				w.doAction(World.A_SHOOT);
				return;
			}
		}
		if(y<4) {
			if(w.isVisited(x, y+1)) {
				movePlayer(x,y+1);
				turnPlayer(2);
				w.doAction(World.A_SHOOT);
				return;
			}
		}
		if(x<4) {
			if(w.isVisited(x+1, y)) {
				movePlayer(x+1,y);
				turnPlayer(3);
				w.doAction(World.A_SHOOT);
				return;
			}
		}
		
	}	

	private void movePlayer(int i, int j) {
		
		int prevx=-1;
		int prevy=-1;
		int thisx=0,thisy=0;
		int x= w.getPlayerX();
		int y = w.getPlayerY();
		System.out.println(x+" "+y+" x and y in move player");
		int diff = Math.abs(i-x)+Math.abs(y-j);
		int total=0;
		
		while(diff>1) {
			
			total+=1;
			if(total>20) break;
			thisx=x;
			thisy=y;
			if(checkNei(x,y)==1) {
				System.out.println(" in peninsula");
				moveOppdirection();
				prevx=thisx;
				prevy=thisy;
				thisx=w.getPlayerX();
				thisy=w.getPlayerY();
				x=w.getPlayerX();
				y=w.getPlayerY();
			}
			
			if(w.isInPit()) w.doAction(World.A_CLIMB);
			
			diff = Math.abs(i-x)+Math.abs(y-j);
			if(diff<=1) break;
			
			thisx=x;
			thisy=y;
			System.out.println("present position" + x+" "+y);
			if(i>x) {
				System.out.println("in i>x "+prevx+" "+prevy);
				if(w.isVisited(x+1, y) && !(prevx==x+1 && prevy==y)) {
					turnPlayer(1);
					w.doAction(World.A_MOVE);
					x=x+1;
				}
				else if(j>y) {
					if(w.isVisited(x, y+1) && !(prevx==x && prevy==y+1)) {
						turnPlayer(0);
						w.doAction(World.A_MOVE);
						y=y+1;
					}else if(w.isVisited(x, y-1) && !(prevx==x && prevy==y-1)){
						turnPlayer(2);
						w.doAction(World.A_MOVE);
						y=y-1;
					}else {
						turnPlayer(3);
						w.doAction(World.A_MOVE);
						x=x-1;
					}
				}else if(j<y) {
					
					if(w.isVisited(x, y-1) && !(prevx==x && prevy==y-1)) {
						System.out.print("in j<y ");
						turnPlayer(2);
						System.out.println("direction facing "+w.getDirection());
						w.doAction(World.A_MOVE);
						y=y-1;
					}else if(w.isVisited(x, y+1) && !(prevx==x && prevy==y+1)){
						turnPlayer(0);
						w.doAction(World.A_MOVE);
						y=y+1;
					}else {
						turnPlayer(3);
						w.doAction(World.A_MOVE);
						x=x-1;
					}
				}else if(j==y) {
					if(j>1) {
						if(w.isVisited(x, y-1) && !(prevx==x && prevy==y-1)) {
							turnPlayer(2);
							w.doAction(World.A_MOVE);
							y=y-1;
						}
					}else if(j<4){
						if(w.isVisited(x, y+1) && !(prevx==x && prevy==y+1)) {
							turnPlayer(0);
							w.doAction(World.A_MOVE);
							y=y+1;
						}
					}else {
						turnPlayer(3);
						w.doAction(World.A_MOVE);
						x=x-1;
					}
				}
			}else if(i<x) {
				if(w.isVisited(x-1, y) && !(prevx==x-1 && prevy==y)) {
					turnPlayer(3);
					w.doAction(World.A_MOVE);
					x=x-1;
				}
				else if(j>y) {
					if(w.isVisited(x, y+1) && !(prevx==x && prevy==y+1)) {
						turnPlayer(0);
						w.doAction(World.A_MOVE);
						y=y+1;
					}else if(w.isVisited(x, y-1) && !(prevx==x && prevy==y-1)){
						turnPlayer(2);
						w.doAction(World.A_MOVE);
						y=y-1;
					}else {
						turnPlayer(1);
						w.doAction(World.A_MOVE);
						x=x+1;
					}
				}else if(j<y) {
					if(w.isVisited(x, y-1) && !(prevx==x && prevy==y-1)) {
						turnPlayer(2);
						w.doAction(World.A_MOVE);
						y=y-1;
					}else if(w.isVisited(x, y+1) && !(prevx==x && prevy==y+1)){
						turnPlayer(0);
						w.doAction(World.A_MOVE);
						y=y+1;
					}else {
						turnPlayer(1);
						w.doAction(World.A_MOVE);
						x=x+1;
					}
				}else if(j==y) {
					if(j>1) {
						if(w.isVisited(x, y-1) && !(prevx==x && prevy==y-1)) {
							turnPlayer(2);
							w.doAction(World.A_MOVE);
							y=y-1;
						}
					}else if(j<4){
						if(w.isVisited(x, y+1) && !(prevx==x && prevy==y+1)) {
							turnPlayer(0);
							w.doAction(World.A_MOVE);
							y=y+1;
						}
					}else {
						turnPlayer(1);
						w.doAction(World.A_MOVE);
						x=x+1;
					}
				}
			}else if(i==x) {
				System.out.println("in i==x "+prevx+" "+prevy);
				if(j>y) {
					if(w.isVisited(x, y+1) && !(prevx==x && prevy==y+1)) {
						turnPlayer(0);
						w.doAction(World.A_MOVE);
						y=y+1;
					}else if(w.isVisited(x-1, y) && !(prevx==x-1 && prevy==y)) {
						turnPlayer(3);
						w.doAction(World.A_MOVE);
						x=x-1;
					}else if(w.isVisited(x+1, y) && !(prevx==x+1 && prevy==y)) {
						turnPlayer(1);
						w.doAction(World.A_MOVE);
						x=x+1;
					}else {
						turnPlayer(2);
						w.doAction(World.A_MOVE);
						y=y-1;
					}
				}else if(j<y) {
					if(w.isVisited(x, y-1) && !(prevx==x && prevy==y-1)) {
						turnPlayer(2);
						w.doAction(World.A_MOVE);
						y=y-1;
					}else if(w.isVisited(x-1, y) && !(prevx==x-1 && prevy==y)) {
						turnPlayer(3);
						w.doAction(World.A_MOVE);
						x=x-1;
					}else if(w.isVisited(x+1, y) && !(prevx==x+1 && prevy==y)) {
						turnPlayer(1);
						w.doAction(World.A_MOVE);
						x=x+1;
					}else {
						turnPlayer(0);
						w.doAction(World.A_MOVE);
						y=y+1;
					}
				}
			}
			if(w.isInPit()) w.doAction(World.A_CLIMB);
			prevx=thisx;
			prevy=thisy;
			diff = Math.abs(i-x)+Math.abs(y-j);
		}
		System.out.println("present end pos "+w.getPlayerX()+" "+w.getPlayerY());
		if(i!=x) {
			if(i>x) {
				System.out.println("in end move i>x"+w.getPlayerX()+w.getPlayerY());
				turnPlayer(1);
				w.doAction(World.A_MOVE);
			}else {
				turnPlayer(3);
				w.doAction(World.A_MOVE);
			}
		}else if(j!=y) {
			if(j>y) {
				turnPlayer(0);
				w.doAction(World.A_MOVE);
			}else {
				turnPlayer(2);
				w.doAction(World.A_MOVE);
			}
		}
		
	}

	private int checkNei(int x, int y) {
		int total=0;
		if(w.isVisited(x-1, y)) total+=1;
		if(w.isVisited(x+1, y)) total+=1;
		if(w.isVisited(x, y+1)) total+=1;
		if(w.isVisited(x, y+1)) total+=1;
		return total;
	}

	private void moveOppdirection() {
		if(w.getDirection()==0) {
			turnPlayer(2);
			w.doAction(World.A_MOVE);
		}
		else if(w.getDirection()==1) {
			turnPlayer(3);
			w.doAction(World.A_MOVE);
		}else if(w.getDirection()==2) {
			turnPlayer(0);
			w.doAction(World.A_MOVE);
		}else if(w.getDirection()==3) {
			turnPlayer(1);
			w.doAction(World.A_MOVE);
		}
		
	}

	private void turnPlayer(int d) {

		//turn towards right
		if(d==1) {
			if(w.getDirection()==0) w.doAction(World.A_TURN_RIGHT);
			if(w.getDirection()==2) w.doAction(World.A_TURN_LEFT);
			if(w.getDirection()==3) {
				w.doAction(World.A_TURN_LEFT);
				w.doAction(World.A_TURN_LEFT);
			}
		}
		else if(d==2) {//turn facing down
			if(w.getDirection()==0) {
				w.doAction(World.A_TURN_LEFT);
				w.doAction(World.A_TURN_LEFT);
			}
			if(w.getDirection()==1) w.doAction(World.A_TURN_RIGHT);
			if(w.getDirection()==3) w.doAction(World.A_TURN_LEFT);
		}
		else if(d==3) {//turn towards left
			if(w.getDirection()==1) {
				w.doAction(World.A_TURN_LEFT);
				w.doAction(World.A_TURN_LEFT);
			}
			if(w.getDirection()==2) w.doAction(World.A_TURN_RIGHT);
			if(w.getDirection()==0) w.doAction(World.A_TURN_LEFT);
		}else if(d==0) {//turn facing up
			if(w.getDirection()==2) {
				w.doAction(World.A_TURN_LEFT);
				w.doAction(World.A_TURN_LEFT);
			}
			if(w.getDirection()==3) w.doAction(World.A_TURN_RIGHT);
			if(w.getDirection()==1) w.doAction(World.A_TURN_LEFT);
		}
	}

	public int convert(int x,int y) {
		return (x-1)*w.getSize()+y-1;
	}

	private void setTrue(boolean[] arr, int x, int y) {
		if(x>1) {
			int k = (x-2) * w.getSize() + y -1;
			arr[k]=true;
			System.out.println(k+" is true");
		}
		if(y>1) {
			int k = (x-1) * w.getSize() + y -2;
			arr[k]=true;
			System.out.println(k+" is true");
		}
		if(x<4) {
			int k = (x)*w.getSize() + y-1;
			arr[k]=true;
			System.out.println(k+" is true");
		}
		if(y<4) {
			int k = (x-1)*w.getSize() + y;
			arr[k]=true;
			System.out.println(k+" is true");
		}
	}
	
	private void setFalse(boolean[] arr, int x, int y) {
		if(x>1) {
			int k = (x-2) * w.getSize() + y -1;
			arr[k]=false;
			System.out.println(k+" is false");
		}
		if(y>1) {
			int k = (x-1) * w.getSize() + y -2;
			arr[k]=false;
			System.out.println(k+" is false");
		}
		if(x<4) {
			int k = (x)*w.getSize() + y-1;
			arr[k]=false;
			System.out.println(k+" is false");
		}
		if(y<4) {
			int k = (x-1)*w.getSize() + y;
			arr[k]=false;
			System.out.println(k+" is false");
		}
		
	}

}
