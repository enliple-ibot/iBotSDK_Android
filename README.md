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
#### Button Setting from java
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
#### Button Setting from xml
```xml
    <com.enliple.ibotsdk.widget.IBotChatButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```