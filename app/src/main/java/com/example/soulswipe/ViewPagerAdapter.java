package com.example.soulswipe;

<<<<<<< HEAD
import android.view.View;

=======
>>>>>>> origin/main
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = new ArrayList<>();
        fragments.add(new CardFragment());
        fragments.add(new MatchFragment());
        fragments.add(new ProfileFragment());
=======
public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
>>>>>>> origin/main
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
<<<<<<< HEAD
        return fragments.get(position);
=======
        switch (position) {
            case 0:
                return CardFragment.getInstance();
            case 1:
                return new MatchFragment();
            case 2:
                return new ProfileFragment();
            default:
                return null;
        }
>>>>>>> origin/main
    }

    @Override
    public int getCount() {
<<<<<<< HEAD
        return fragments.size();
=======
        return 3;
>>>>>>> origin/main
    }
}
