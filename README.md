# iBotSDK_Android
---
### build.gradle
 - 내 build.gradle에 implementation 'androidx.appcompat:appcompat 가 정의되어 있을 경우

   implementation ('com.enliple:ibotsdk:sdk-version') {
       exclude group: 'androidx.appcompat',  module: 'appcompat'
   }

 - 내 build.gradle에  implementation 'androidx.appcompat:appcompat가 정의되어 있지 않을 경우
 
   implementation 'com.enliple:ibotsdk:sdk-version'
### Init and setButton
```java
public class MainActivity extends AppCompatActivity {

    private LinearLayout buttonLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout buttonLayer = findViewById(R.id.buttonLayer);

        IBotSDK sdk = new IBotSDK(getApplicationContext(), "발급받은 api key");
        sdk.openIBotWithBrowser();
        sdk.showIBotButton(MainActivity.this, true, IBotChatButton.TYPE_RIGHT_TO_LEFT_EXPANDABLE_BUTTON, buttonLayer);
    }
}
```
#### About Function
private void initSDK(final Context context, final String apiKey)
 - apiKey : 발급받은 api key

public void openIBotWithBrowser()
 IBot chatting창을 외부 브라우저로 열고 싶을 경우 설정

public void showIBotButton(final Context context, boolean isShow, int type, ViewGroup view)
 - isShow : 버튼을 노출 시키고 싶을 경우 true. 그렇지 않으면 false
 - type : 아래 type 참조
 - view : IBot 버튼을 노출시키고 싶은 view (LinearLayout, RelativeLayout etc.)

#### Type
   - IBotChatButton.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON : 버튼이 화면 오른쪽에 위치하며 확장 영역이 우측에서 좌측으로 늘어남

   ![screenshot_right_300](https://user-images.githubusercontent.com/56538133/66888820-00d12300-f01b-11e9-9cb8-2c62bd402b2e.jpg)
   - IBotChatButton.TYPE_LEFT_TO_RIGHT_EXPANDABLE_BUTTON : 버튼이 화면 왼쪽에 위치하며 확장 영역이 좌측에서 우측으로 늘어남

   ![Screenshot_left_300](https://user-images.githubusercontent.com/56538133/66888821-0169b980-f01b-11e9-81f8-dd9817720f9d.jpg)
   - IBotChatButton.TYPE_NON_EXPANDABLE_BUTTON : 버튼만 존재하며 확장영역이 나타나지 않음

   ![showbot_icon_50](https://user-images.githubusercontent.com/56538133/66888822-0169b980-f01b-11e9-8501-9540a4fc1408.png)



