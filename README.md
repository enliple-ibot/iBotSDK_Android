# iBotSDK_Android

## Requirements
* minSdkVersion 15

---
### Download
use Gradle:

```xml
   dependencies {
      implementation 'com.enliple:ibotsdk:0.0.55'
   }
```
or use Maven:

```xml
<dependency>
  <groupId>com.enliple</groupId>
  <artifactId>ibotsdk</artifactId>
  <version>0.0.55</version>
  <type>pom</type>
</dependency>
```

### Init and setButton
```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout buttonLayer = findViewById(R.id.buttonLayer);

        IBotSDK sdk = new IBotSDK(getApplicationContext(), "발급받은 api key");
        sdk.openIBotWithBrowser();
        sdk.setChatActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sdk.showIBotButton(MainActivity.this, true, true, IBotChatButton.TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON, "#ffffff", buttonLayer);
    }
}
```
#### About Function
public void openIBotWithBrowser()
 IBot 채팅창을 외부 브라우저로 열고 싶을 경우 설정

public void setChatActivityOrientation(int orientation)
 IBot 채팅창의 orientation 설정

public void showIBotButton(final Context context, boolean isShow, final boolean isDraggable, int type, String bgColor, ViewGroup view)
 - isShow : 버튼을 노출 시키고 싶을 경우 true. 그렇지 않으면 false
 - isDraggable : 버튼을 touch and drag가 가능하게 하려면 true. 그렇지 않으면 false
 - type : 아래 type 참조
 - bgColor : 버튼의 배경 색 ( 버튼 이미지에 투명색이 포함됬을 경우 이를 대체하기 위함 ), 아래 Color 참조
 - view : IBot 버튼을 노출시키고 싶은 view (LinearLayout, RelativeLayout etc.)

public void showIBotInBrowser()
 IBotButton의 클릭 없이 외부 브라우저로 바로 채팅창을 열고 싶을 경우 호출

 ```java
  IBotSDK sdk = new IBotSDK(getApplicationContext(), "발급받은 api key");
  sdk.showIBotInBrowser();
```

#### Type
   - IBotChatButton.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON : 버튼이 화면 오른쪽에 위치하며 확장 영역이 우측에서 좌측으로 늘어남

   ![screenshot_right_300](https://user-images.githubusercontent.com/56538133/66888820-00d12300-f01b-11e9-9cb8-2c62bd402b2e.jpg)
   - IBotChatButton.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON : 버튼이 화면 왼쪽에 위치하며 확장 영역이 좌측에서 우측으로 늘어남

   ![Screenshot_left_300](https://user-images.githubusercontent.com/56538133/66888821-0169b980-f01b-11e9-81f8-dd9817720f9d.jpg)
   - IBotChatButton.TYPE_NON_EXPANDABLE_BUTTON : 버튼만 존재하며 확장영역이 나타나지 않음

   ![showbot_icon_50](https://user-images.githubusercontent.com/56538133/66888822-0169b980-f01b-11e9-8501-9540a4fc1408.png)


#### Color
  - 3자리, 6자리 hex code 값
    ex) #fff, #ffffff
    * alpha 값이 포함된 8자리 값은 사용 안됨. 만일 alpha 값이 포함될 경우 맨 앞 2자리 삭제
    ex) #ffaaaaaa -> #aaaaaa로 적용됨




