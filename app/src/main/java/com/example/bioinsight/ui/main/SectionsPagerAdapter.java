package com.example.bioinsight.ui.main;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.bioinsight.PlaceholderFragment;
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new Atlas1Fragment();
            case 2:
                return new Atlas2Fragment();
            case 3:
                return new ModbusFragment();
            case 4:
                return new NuevoUsuario();
            case 5:
                return new GestionarUsuarios();
            default:
                return new PlaceholderFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Home";
            case 1: return "Atlas1";
            case 2: return "Atlas2";
            case 3: return "Modbus";
            case 4: return "Nuevo Usuario";
            case 5: return "Gestionar Usuarios";
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}