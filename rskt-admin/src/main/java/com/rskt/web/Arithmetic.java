package com.rskt.web;

import org.springframework.stereotype.Component;

/**
 * @author wangjiayuan
 */
@Component
public class Arithmetic {

    public static void main(String args[]) throws InterruptedException {
        //多线程计算最小公约数最大公倍数
//        ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();
//        ThreadPoolTaskExecutor thread = threadPoolConfig.threadPoolTaskExecutor();
//        int num = 100;
//        System.out.println("开始计算");
//        for (int i = 1; i < num; i++) {
//                thread.submit(new MyThread(i,num));
//        }
//        ThreadPoolExecutor pool = thread.getThreadPoolExecutor();
//        int a=0;
//        while (pool.getCompletedTaskCount() != num-1){
//            if (a%100 == 0){
//                System.out.println("子线程还在运行");
//            }
//            Thread.sleep(1000);
//            a++;
//        }
//        System.out.println("计算结束");
//        thread.shutdown();

    }

    //求出m,n的最小公约数
    public static int commonDivisor(int m , int n) {
        if (m <=0 || n<=0){
            return 0;
        }
        for (int i =2; i<= m/2 ;i++){
            if (m%i == 0 ){
                for (int a = 2; a<= n/2;a++){
                    if (n%a == 0){
                        if (i == a){
                            return i;
                        }
                    }
                }
                break;
            }
        }
        return 0;
    }

    //求出m,n的最小公倍数
    public static int commonMultiple(int m , int n) {
        if (m <=0 || n<=0){
            return 0;
        }
        if (m > n){
            if (m%n == 0){
                return m;
            }
        }else {
            if (n%m == 0){
                return n;
            }
        }
        if (commonDivisor(m,n) == 0){
            return m*n;
        }
        return (m*n)/commonDivisor(m,n);
    }

    //分解输入为最小的质数
    public static void fengjie(int n){
        for(int i=2;i<=n/2;i++){
            if(n%i==0){
                System.out.print(i+"*");
                fengjie(n/i);
                break;
            }
        }
        for (int a = 2; a <= n-1 ; a++) {
            if (n % a != 0 && n<10) {
                System.out.print(n);
                break;
            }
        }
    }

    //判断是否为水仙花数
    public static boolean shuixianhua(int x)
    {
        int i=0,j=0,k=0;
        i=x / 100;
        j=(x % 100) /10;
        k=x % 10;
        return x == i * i * i + j * j * j + k * k * k;
    }

    //判断输入是否为素数
    public static boolean iszhishu(int x)
    {
        for(int i=2;i<=x/2;i++) {
            if (x % 2==0 ) {
                return false;
            }
        }
        return true;
    }

    //题目：古典问题：有一对兔子，从出生后第3个月起每个月都生一对兔子，小兔子长到第四个月后每个月又生一对兔子，假如兔子都不死，问每个月的兔子总数为多少？
    public static int f(double x)
    {
        if(x==1 || x==2) {
            return 1;
        } else {
            return f(x-1)+f(x-2);
        }
    }


}
