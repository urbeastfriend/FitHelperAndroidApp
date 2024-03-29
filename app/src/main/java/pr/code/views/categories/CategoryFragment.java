package pr.code.views.categories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pr.code.R;
import pr.code.adapters.RecyclerViewRecipesByCategory;
import pr.code.api.FoodClient;
import pr.code.models.Meals;
import pr.code.utils.DBHelper;
import pr.code.utils.ApiNDialogHelper;
import pr.code.utils.FavoritesListHelper;
import pr.code.views.recipedetails.DetailsActivity;

import static pr.code.views.recipes.RecipesFragment.EXTRA_DETAIL;

/**
 * This fragment used within CategoryActivity and responsible for recipes by category list view building
 */
public class CategoryFragment extends Fragment implements CategoryView {

    @BindView(R.id.recyclerViewwww)
    RecyclerView recyclerViewww;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.imageCategory)
    ImageView imageCategory;
    @BindView(R.id.imageCategoryBg)
    ImageView imageCategoryBg;
    @BindView(R.id.textCategory)
    TextView textCategory;

    AlertDialog.Builder descDialog;
    Context con;

    static SQLiteDatabase database;
    static CategoryPresenter presenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category,container,false);
        ButterKnife.bind(this,view);
        return view;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        con = context;
        super.onAttach(context);
    }

    void initvalues(CategoryFragment view){
        database = DBHelper.getInstance(con).getReadableDatabase();
        presenter = new CategoryPresenter(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments()!=null){
            textCategory.setText(getArguments().getString("EXTRA_DATA_DESC"));
            Picasso.get().load(FoodClient.getBaseUrl() + getArguments().getString("EXTRA_DATA_IMAGE")).networkPolicy(NetworkPolicy.OFFLINE).resize(320,200)
                    .into(imageCategory, new Callback() {
                        @Override
                        public void onSuccess() { }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(FoodClient.getBaseUrl() + getArguments().getString("EXTRA_DATA_IMAGE")).resize(320,200).error(R.drawable.ic_error_recipe)
                                    .into(imageCategory);
                        }
                    });

            Picasso.get().load(FoodClient.getBaseUrl() + getArguments().getString("EXTRA_DATA_IMAGE")).networkPolicy(NetworkPolicy.OFFLINE).resize(320,200)
                    .into(imageCategoryBg, new Callback() {
                        @Override
                        public void onSuccess() { }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(FoodClient.getBaseUrl() + getArguments().getString("EXTRA_DATA_IMAGE")).resize(320,200).error(R.drawable.ic_error_recipe)
                                    .into(imageCategoryBg);
                        }
                    });

            initvalues(this);


            presenter.getMealByCategory(getArguments().getString("EXTRA_DATA_NAME"),database);


        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setMeals(List<Meals.Meal> meals,List<String> favlist) {
        RecyclerViewRecipesByCategory adapter = new RecyclerViewRecipesByCategory(getActivity(),meals,favlist);
        recyclerViewww.setLayoutManager(new GridLayoutManager(con,2));
        recyclerViewww.setClipToPadding(false);
        recyclerViewww.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnitemClickListener(((view, position) -> {
            TextView mealname = view.findViewById(R.id.mealName);
            Intent intent = new Intent(con, DetailsActivity.class);
            intent.putExtra(EXTRA_DETAIL, mealname.getText().toString());
            startActivity(intent);
        }));
    }

    @Override
    public void onErrorLoading(String message) {
        ApiNDialogHelper.showDialogMessage(getActivity(),"Error ",message);
    }

    @OnClick(R.id.cardCategory)
    public void onClick(){
        descDialog = new AlertDialog.Builder(con);
        descDialog.setMessage(getArguments().getString("EXTRA_DATA_DESC"));
        descDialog.setPositiveButton("ЗАКРЫТЬ",((dialog, which) -> dialog.dismiss()));
        AlertDialog ad = descDialog.create();
        ad.show();
    }

    public static void addToFavorite(String id){
        FavoritesListHelper.addToFavorites(database,id);
    }

    public static void removeFromFavorite(String id){
        FavoritesListHelper.addToFavorites(database,id);
    }


}