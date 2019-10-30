# iBotSDK_Android
---
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




