package com.example.scanner;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import java.util.concurrent.atomic.AtomicInteger;


public class ScannerPlugin
        implements Application.ActivityLifecycleCallbacks,
        FlutterPlugin,
        ActivityAware,
        DefaultLifecycleObserver, MethodChannel.MethodCallHandler {
    static final int CREATED = 1;
    static final int STARTED = 2;
    static final int RESUMED = 3;
    static final int PAUSED = 4;
    static final int STOPPED = 5;
    static final int DESTROYED = 6;
    private final AtomicInteger state = new AtomicInteger(0);
    private FlutterPluginBinding pluginBinding;
    private Lifecycle lifecycle;
    static MethodChannel methodChannel;

    private static final String VIEW_TYPE = "scanner";



    public ScannerPlugin() {
    }

    // FlutterPlugin


    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {

final MethodChannel channel =
    new MethodChannel(binding.getBinaryMessenger(), "scanner");
channel.setMethodCallHandler(this);   // use THIS instance
methodChannel = channel;
        pluginBinding = binding;
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        pluginBinding = null;
    }

    // ActivityAware

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(binding);
binding.getActivity()
       .getApplication()
       .registerActivityLifecycleCallbacks(this);

        lifecycle.addObserver(this);
        pluginBinding
                .getPlatformViewRegistry()
                .registerViewFactory(
                        VIEW_TYPE,
                        new ScannerFactory(
                                state,
                                pluginBinding.getBinaryMessenger(),
                                binding.getActivity().getApplication(),
                                lifecycle,
                                null,
                                binding.getActivity().hashCode(), binding.getActivity(), methodChannel));
    }

    @Override
    public void onDetachedFromActivity() {
        lifecycle.removeObserver(this);
                // Also stop receiving application-level callbacks we registered earlier
    if (pluginBinding != null &&
        pluginBinding.getApplicationContext() instanceof Application) {
        ((Application) pluginBinding.getApplicationContext())
            .unregisterActivityLifecycleCallbacks(this);
    }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(binding);
        lifecycle.addObserver(this);
    }

    // DefaultLifecycleObserver methods

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        state.set(CREATED);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        state.set(STARTED);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        state.set(RESUMED);
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        state.set(PAUSED);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        state.set(STOPPED);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        state.set(DESTROYED);
    }

    // Application.ActivityLifecycleCallbacks methods (used only when the plugin
    // is attached via the old V1 registrar — kept for backward compatibility,
    // but all registrarActivityHashCode checks are now gone)
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        state.set(CREATED);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        state.set(STARTED);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        state.set(RESUMED);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        state.set(PAUSED);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        state.set(STOPPED);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activity.getApplication().unregisterActivityLifecycleCallbacks(this);
        state.set(DESTROYED);
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
if (methodCall.method.equals("getPlatformVersion")) {
    result.success("Android " + android.os.Build.VERSION.RELEASE);
} else {
    result.notImplemented();
}
    }
}
