// Generated by view binder compiler. Do not edit!
package com.example.bioinsight.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.bioinsight.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentHomeBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView avatarUsuario;

  @NonNull
  public final Button btnActualizar;

  @NonNull
  public final TextView datosUsuario;

  @NonNull
  public final TextView textoSaludo;

  private FragmentHomeBinding(@NonNull CardView rootView, @NonNull ImageView avatarUsuario,
      @NonNull Button btnActualizar, @NonNull TextView datosUsuario,
      @NonNull TextView textoSaludo) {
    this.rootView = rootView;
    this.avatarUsuario = avatarUsuario;
    this.btnActualizar = btnActualizar;
    this.datosUsuario = datosUsuario;
    this.textoSaludo = textoSaludo;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentHomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.avatar_usuario;
      ImageView avatarUsuario = ViewBindings.findChildViewById(rootView, id);
      if (avatarUsuario == null) {
        break missingId;
      }

      id = R.id.btn_actualizar;
      Button btnActualizar = ViewBindings.findChildViewById(rootView, id);
      if (btnActualizar == null) {
        break missingId;
      }

      id = R.id.datos_usuario;
      TextView datosUsuario = ViewBindings.findChildViewById(rootView, id);
      if (datosUsuario == null) {
        break missingId;
      }

      id = R.id.texto_saludo;
      TextView textoSaludo = ViewBindings.findChildViewById(rootView, id);
      if (textoSaludo == null) {
        break missingId;
      }

      return new FragmentHomeBinding((CardView) rootView, avatarUsuario, btnActualizar,
          datosUsuario, textoSaludo);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
