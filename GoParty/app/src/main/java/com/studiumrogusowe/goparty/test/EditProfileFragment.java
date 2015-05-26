package com.studiumrogusowe.goparty.test;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.studiumrogusowe.goparty.R;
import com.studiumrogusowe.goparty.profile.api.ProfileRestAdapter;
import com.studiumrogusowe.goparty.profile.api.model.ProfileBodyObject;
import com.studiumrogusowe.goparty.profile.api.model.ProfileResponseObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileFragment extends Fragment {

    EditText name,lastname,favGenres,favBands;
    Button save;
    String token;
    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        name = (EditText) view.findViewById(R.id.editProfileName);
        lastname = (EditText) view.findViewById(R.id.editProfileLastname);
        favGenres = (EditText) view.findViewById(R.id.editProfileFavGenres);
        favBands = (EditText) view.findViewById(R.id.editProfileFavBands);
        save = (Button) view.findViewById(R.id.editProfileSave);

        SharedPreferences sp = this.getActivity().getSharedPreferences("com.studiumrogusowe.goparty", Context.MODE_PRIVATE);
        token = sp.getString("token","Bearer 0");
        Log.d("profiletoken", token);

        ProfileRestAdapter.getInstance().getProfileApi().getProfile(token, new Callback<ProfileResponseObject>() {
            @Override
            public void success(ProfileResponseObject profileResponseObject, Response response) {


                // setting name fields etc
                name.setText(profileResponseObject.getFirst_name());
                lastname.setText(profileResponseObject.getLast_name());
                favBands.setText(profileResponseObject.getFavourite_bands().toString().replace("[","").replace("]",""));
                favGenres.setText(profileResponseObject.getFavourite_genres().toString().replace("[", "").replace("]", ""));


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileBodyObject profileBodyObject = new ProfileBodyObject();
                profileBodyObject.setFirst_name(name.getText().toString());
                profileBodyObject.setLast_name(lastname.getText().toString());
                profileBodyObject.setFavourite_bands(profileBodyObject.convertEditTextToList(favBands));
                profileBodyObject.setFavourite_genres(profileBodyObject.convertEditTextToList(favGenres));

                // executing request
                ProfileRestAdapter.getInstance().getProfileApi().editProfile(token, profileBodyObject, new Callback<String>() {


                    @Override
                    public void success(String callback, Response response) {
                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();

                        Fragment fragment = new UserProfileFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment).commit();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();

                        Fragment fragment = new UserProfileFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment).commit();
                    }
                });

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
