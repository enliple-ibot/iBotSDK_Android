# iBotSDK_Android
---
### Init and setButton
#### init
```java
public class MainActivity extends AppCompatActivity {

    private LinearLayout buttonLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout buttonLayer = findViewById(R.id.buttonLayer);

        IBotSDK.instance.initSDK("발급받은 api key");
    }
}
```
#### Button Setting
```java
public class MainActivity extends AppCompatActivity {

    private LinearLayout buttonLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout buttonLayer = findViewById(R.id.buttonLayer);

        IBotSDK.instance.initSDK("발급받은 api key");
        IBotSDK.instance.showIBotButton(MainActivity.this, true, buttonLayer);
    }
}
```

### IBotChatButton Customizing
<img src="https://user-images.githubusercontent.com/56538133/66885715-1b51cf00-f010-11e9-94c5-4bf7954b02bd.png" width="520" height="195">

**attribute**
- buttonBg : 버튼의 이미지 설정(정사각형 이미지)
- barBg : 확장 영역의 배경 색상값 설정
- barText : 확장 영역의 문자열
- barTextColor : 확장 영역 문자열의 색상
- barTextSize : 확장 영역의 문자열 크기
- buttonCloseIcon : 확장영역 닫기 버튼 이미지 설정(정사각형)
- size : button의 size 
   - app:size=“60” -> buttonBg에 설정된 image size 60dp X 60dp
- type : 0 or 1 or 2
   - 0 : 버튼이 화면 오른쪽에 위치하며 확장 영역이 좌에서 우로 늘어남   
   ![screenshot_right_300](https://user-images.githubusercontent.com/56538133/66888820-00d12300-f01b-11e9-9cb8-2c62bd402b2e.jpg)
   - 1 : 버튼이 화면 왼쪾에 위치하며 확장 영역이 우에서 좌로 늘어남   
   ![Screenshot_left_300](https://user-images.githubusercontent.com/56538133/66888821-0169b980-f01b-11e9-81f8-dd9817720f9d.jpg)
   - 2 : 버튼만 존재하며 확장영역이 나타나지 않음   
   ![showbot_icon_50](https://user-images.githubusercontent.com/56538133/66888822-0169b980-f01b-11e9-8501-9540a4fc1408.png)
   
**Example**
```xml
    <com.enliple.ibotsdk.widget.IBotChatButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:buttonBg="@drawable/ibot_icon"
        app:barBg="@color/ibot_bar_background"
        app:barText="@string/hello_ibot"
        app:barTextColor="@color/ibot_text_color"
        app:barTextSize="15"
        app:buttonCloseIcon="@drawable/ibot_ico_close"
        app:size="60"
        app:type="0"/>
```
---
