##Screenshot
![img](https://github.com/woxingxiao/DashboardViewDemo/blob/master/screenshot/Screenshot5.png)
![img](https://github.com/woxingxiao/DashboardViewDemo/blob/master/screenshot/Screenshot6.gif)

##Usage
>1

```xml
<com.xw.example.dashboardviewdemo.DashboardView
    android:id="@+id/default_dashboard_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```
>2

```xml
<com.xw.example.dashboardviewdemo.DashboardView
    android:id="@+id/dashboard_view_2"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:bigSliceCount="9"
    app:headerRadius="50dp"
    app:headerTitle="kW"
    app:maxValue="450"
    app:measureTextSize="10sp"
    app:radius="110dp"
    app:realTimeValue="325"
    app:startAngle="210"
    app:stripeWidth="30dp"
    app:sweepAngle="120"/>
```
```java
List<HighlightCR> highlight1 = new ArrayList<>();
highlight1.add(new HighlightCR(210, 60, Color.parseColor("#03A9F4")));
highlight1.add(new HighlightCR(270, 60, Color.parseColor("#FFA000")));
dashboardView2.setStripeHighlightColorAndRange(highlight1);

// play animation
dashboardView2.setRealTimeValueWithAnim(150.f);
```
>3

```xml
<com.xw.example.dashboardviewdemo.DashboardView
    android:id="@+id/dashboard_view_3"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:bigSliceCount="12"
    app:headerRadius="50dp"
    app:headerTitle="km/h"
    app:maxValue="240"
    app:measureTextSize="10sp"
    app:radius="110dp"
    app:sliceCountInOneBigSlice="2"
    app:realTimeValue="80"
    app:startAngle="170"
    app:stripeMode="inner"
    app:stripeWidth="40dp"
    app:sweepAngle="200"/>
```
```java
List<HighlightCR> highlight2 = new ArrayList<>();
highlight2.add(new HighlightCR(170, 140,Color.parseColor("#607D8B")));
highlight2.add(new HighlightCR(310, 60, Color.parseColor("#795548")));
dashboardView3.setStripeHighlightColorAndRange(highlight2);
```
>4

```xml
<com.xw.example.dashboardviewdemo.DashboardView
    android:id="@+id/dashboard_view_4"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```
```java
dashboardView4.setRadius(110);
dashboardView4.setArcColor(getResources().getColor(android.R.color.black));
dashboardView4.setTextColor(Color.parseColor("#212121"));
dashboardView4.setBgColor(getResources().getColor(android.R.color.white));
dashboardView4.setStartAngle(150);
dashboardView4.setPointerRadius(80);
dashboardView4.setCircleRadius(8);
dashboardView4.setSweepAngle(240);
dashboardView4.setBigSliceCount(12);
dashboardView4.setSliceCountInOneBigSlice(2);
dashboardView4.setMaxValue(240);
dashboardView4.setRealTimeValue(80);
dashboardView4.setMeasureTextSize(14);
dashboardView4.setHeaderRadius(50);
dashboardView4.setHeaderTitle("km/h");
dashboardView4.setHeaderTextSize(16);
dashboardView4.setStripeWidth(20);
dashboardView4.setStripeMode(DashboardView.StripeMode.OUTER);
List<HighlightCR> highlight3 = new ArrayList<>();
highlight3.add(new HighlightCR(150, 100, Color.parseColor("#4CAF50")));
highlight3.add(new HighlightCR(250, 80, Color.parseColor("#FFEB3B")));
highlight3.add(new HighlightCR(330, 60, Color.parseColor("#F44336")));
dashboardView4.setStripeHighlightColorAndRange(highlight3);
```
##Attributes
```xml
<attr name="radius" format="dimension"/>     <!--扇形半径-->
<attr name="startAngle" format="integer"/> <!-- 起始角度-->
<attr name="sweepAngle" format="integer"/>  <!--绘制角度-->
<attr name="bigSliceCount" format="integer"/> <!-- 长刻度条数-->
<attr name="sliceCountInOneBigSlice" format="integer"/> <!-- 长刻度条数-->
<attr name="arcColor" format="color"/> <!--弧度颜色-->
<attr name="measureTextSize" format="dimension"/> <!--刻度字体大小-->
<attr name="textColor" format="color"/> <!--字体颜色-->
<attr name="headerTitle" format="string"/> <!--表头-->
<attr name="headerTextSize" format="string"/> <!--表头字体大小-->
<attr name="headerRadius" format="dimension"/> <!--表头半径-->
<attr name="pointerRadius" format="dimension"/> <!--指针半径-->
<attr name="circleRadius" format="dimension"/> <!--中心圆半径-->
<attr name="minValue" format="integer"/> <!--最小值-->
<attr name="maxValue" format="integer"/> <!--最大值-->
<attr name="realTimeValue" format="float"/> <!--实时值-->
<attr name="stripeWidth" format="dimension"/> <!--色带宽度-->
<attr name="stripeMode" > <!--色条显示位置-->
    <enum name="normal" value="0"/>
    <enum name="inner" value="1"/>
    <enum name="outer" value="2"/>
</attr>
<attr name="bgColor" format="color"/> <!--背景颜色-->
```
##codes
###[DashboardView.java](https://github.com/woxingxiao/DashboardViewDemo/blob/master/app/src/main/java/com/xw/example/dashboardviewdemo/DashboardView.java)
###[HighlightCR.java](https://github.com/woxingxiao/DashboardViewDemo/blob/master/app/src/main/java/com/xw/example/dashboardviewdemo/HighlightCR.java)
###[attrs.xml](https://github.com/woxingxiao/DashboardViewDemo/blob/master/app/src/main/res/values/attrs.xml)
