// Generated by view binder compiler. Do not edit!
package com.example.bioinsight.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import androidx.viewpager2.widget.ViewPager2;
import com.example.bioinsight.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btnContacto;

  @NonNull
  public final Button btnIniciarSesion;

  @NonNull
  public final EditText contactoEmail;

  @NonNull
  public final EditText contactoMensaje;

  @NonNull
  public final EditText contactoNombre;

  @NonNull
  public final LinearLayout contentLayoutF;

  @NonNull
  public final LinearLayout contentLayoutH;

  @NonNull
  public final LinearLayout contentLayoutV;

  @NonNull
  public final ImageButton imageButton;

  @NonNull
  public final ViewPager2 imageSlider;

  @NonNull
  public final ConstraintLayout main;

  @NonNull
  public final ScrollView scrollView;

  @NonNull
  public final TextView textView;

  @NonNull
  public final TextView textView5;

  @NonNull
  public final TextView textView9;

  @NonNull
  public final TextView titleText;

  private ActivityMainBinding(@NonNull ConstraintLayout rootView, @NonNull Button btnContacto,
      @NonNull Button btnIniciarSesion, @NonNull EditText contactoEmail,
      @NonNull EditText contactoMensaje, @NonNull EditText contactoNombre,
      @NonNull LinearLayout contentLayoutF, @NonNull LinearLayout contentLayoutH,
      @NonNull LinearLayout contentLayoutV, @NonNull ImageButton imageButton,
      @NonNull ViewPager2 imageSlider, @NonNull ConstraintLayout main,
      @NonNull ScrollView scrollView, @NonNull TextView textView, @NonNull TextView textView5,
      @NonNull TextView textView9, @NonNull TextView titleText) {
    this.rootView = rootView;
    this.btnContacto = btnContacto;
    this.btnIniciarSesion = btnIniciarSesion;
    this.contactoEmail = contactoEmail;
    this.contactoMensaje = contactoMensaje;
    this.contactoNombre = contactoNombre;
    this.contentLayoutF = contentLayoutF;
    this.contentLayoutH = contentLayoutH;
    this.contentLayoutV = contentLayoutV;
    this.imageButton = imageButton;
    this.imageSlider = imageSlider;
    this.main = main;
    this.scrollView = scrollView;
    this.textView = textView;
    this.textView5 = textView5;
    this.textView9 = textView9;
    this.titleText = titleText;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnContacto;
      Button btnContacto = ViewBindings.findChildViewById(rootView, id);
      if (btnContacto == null) {
        break missingId;
      }

      id = R.id.btnIniciarSesion;
      Button btnIniciarSesion = ViewBindings.findChildViewById(rootView, id);
      if (btnIniciarSesion == null) {
        break missingId;
      }

      id = R.id.contactoEmail;
      EditText contactoEmail = ViewBindings.findChildViewById(rootView, id);
      if (contactoEmail == null) {
        break missingId;
      }

      id = R.id.contactoMensaje;
      EditText contactoMensaje = ViewBindings.findChildViewById(rootView, id);
      if (contactoMensaje == null) {
        break missingId;
      }

      id = R.id.contactoNombre;
      EditText contactoNombre = ViewBindings.findChildViewById(rootView, id);
      if (contactoNombre == null) {
        break missingId;
      }

      id = R.id.contentLayoutF;
      LinearLayout contentLayoutF = ViewBindings.findChildViewById(rootView, id);
      if (contentLayoutF == null) {
        break missingId;
      }

      id = R.id.contentLayoutH;
      LinearLayout contentLayoutH = ViewBindings.findChildViewById(rootView, id);
      if (contentLayoutH == null) {
        break missingId;
      }

      id = R.id.contentLayoutV;
      LinearLayout contentLayoutV = ViewBindings.findChildViewById(rootView, id);
      if (contentLayoutV == null) {
        break missingId;
      }

      id = R.id.imageButton;
      ImageButton imageButton = ViewBindings.findChildViewById(rootView, id);
      if (imageButton == null) {
        break missingId;
      }

      id = R.id.imageSlider;
      ViewPager2 imageSlider = ViewBindings.findChildViewById(rootView, id);
      if (imageSlider == null) {
        break missingId;
      }

      ConstraintLayout main = (ConstraintLayout) rootView;

      id = R.id.scrollView;
      ScrollView scrollView = ViewBindings.findChildViewById(rootView, id);
      if (scrollView == null) {
        break missingId;
      }

      id = R.id.textView;
      TextView textView = ViewBindings.findChildViewById(rootView, id);
      if (textView == null) {
        break missingId;
      }

      id = R.id.textView5;
      TextView textView5 = ViewBindings.findChildViewById(rootView, id);
      if (textView5 == null) {
        break missingId;
      }

      id = R.id.textView9;
      TextView textView9 = ViewBindings.findChildViewById(rootView, id);
      if (textView9 == null) {
        break missingId;
      }

      id = R.id.titleText;
      TextView titleText = ViewBindings.findChildViewById(rootView, id);
      if (titleText == null) {
        break missingId;
      }

      return new ActivityMainBinding((ConstraintLayout) rootView, btnContacto, btnIniciarSesion,
          contactoEmail, contactoMensaje, contactoNombre, contentLayoutF, contentLayoutH,
          contentLayoutV, imageButton, imageSlider, main, scrollView, textView, textView5,
          textView9, titleText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
