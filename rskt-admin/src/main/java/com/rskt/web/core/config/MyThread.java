//package com.rskt.web.core.config;
//
//import com.rskt.web.Arithmetic;
//
///**
// * @author wangjiayuan
// */
//public class MyThread implements Runnable {
//    private final Integer i;
//
//    private Integer a;
//
//    public MyThread(int i,int a) {
//        this.i = i;
//        this.a = a;
//    }
//
//
////        for (int a = 0; a < 100;a++) {
//
////    }
//
//
//    @Override
//    public void run() {
//        for (int a=1; a < this.a ;a++){
//            try {
//                System.out.println("m="+i+",n="+a);
//                System.out.println("最小公约数为"+Arithmetic.commonDivisor(i, a));
//                System.out.println("最小公倍数为"+Arithmetic.commonMultiple(i, a));
//            }catch (Exception e){
//                System.out.println(e.getMessage());
//            }
//        }
//    }
//}
