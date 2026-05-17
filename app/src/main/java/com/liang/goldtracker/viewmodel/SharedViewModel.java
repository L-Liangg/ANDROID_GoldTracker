package com.liang.goldtracker.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.liang.goldtracker.GoldTrackerApp;
import com.liang.goldtracker.data.model.GoldPrice;
import com.liang.goldtracker.data.repository.GoldRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Map<String, GoldPrice>> goldPrices = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();

    private String selectedGoldKey;
    private Set<String> pinnedKeys;

    private static final String PREF_NAME       = "user_prefs";
    private static final String PREF_PINNED     = "pinned_gold_keys";
    private static final String PREF_SELECTED   = "selected_gold_key";

    private static final SharedPreferences prefs = GoldTrackerApp.getInstance()
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    public SharedViewModel() {
        fetchAllPrices();
    }

    public LiveData<Map<String, GoldPrice>> getGoldPrices() { return goldPrices; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Throwable> getError() { return error; }

    public String getSelectedGoldKey() {
        if (selectedGoldKey == null) {
            selectedGoldKey = prefs.getString(PREF_SELECTED, null);
        }
        return selectedGoldKey;
    }

    public void setSelectedGoldKey(String key) {
        this.selectedGoldKey = key;
        prefs.edit().putString(PREF_SELECTED, key).apply();
    }

    public Set<String> getPinnedKeys() {
        if (pinnedKeys == null) {
            pinnedKeys = new HashSet<>(prefs.getStringSet(PREF_PINNED, new HashSet<>()));
        }
        return pinnedKeys;
    }

    public void togglePin(String key) {
        if (pinnedKeys == null) getPinnedKeys();
        if (pinnedKeys.contains(key)) {
            pinnedKeys.remove(key);
        } else {
            pinnedKeys.add(key);
        }
        prefs.edit().putStringSet(PREF_PINNED, pinnedKeys).apply();
    }

    public boolean isPinned(String key) {
        if (pinnedKeys == null) getPinnedKeys();
        return pinnedKeys.contains(key);
    }

    public void clearError() {
        error.setValue(null);
    }

    public void fetchAllPrices() {
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                Map<String, GoldPrice> result = GoldRepository.getAllPrices();
                goldPrices.postValue(result);
            } catch (Exception e) {
                error.postValue(e);
            } finally {
                isLoading.postValue(false);
            }
        }).start();
    }
}
