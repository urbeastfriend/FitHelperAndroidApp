package pr.code.views.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pr.code.R;
import pr.code.adapters.FilteredRecipesRecyclerViewAdapter;
import pr.code.models.Meals;
import pr.code.utils.ApiNDialogHelper;
import pr.code.utils.DBHelper;
import pr.code.utils.FavoritesListHelper;
import pr.code.views.recipedetails.DetailsActivity;

import static pr.code.views.recipes.RecipesFragment.EXTRA_DETAIL;

/**
 * This Activity allows to search a collection of recipes by name or tags
 */
public class SearchActivity extends AppCompatActivity implements SearchView{

    @BindView(R.id.searchRecipesEditText)
    EditText editText;

    @BindView(R.id.searchRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.searchToolbar)
    Toolbar toolbar;

    static SQLiteDatabase database;
    static SearchPresenter presenter;

    private FilteredRecipesRecyclerViewAdapter adapter;

    public static final String EXTRA_INSTRUCTIONS = "instructions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initvalues(this);
        setTitle("Поиск рецептов");

        if(getIntent().getStringArrayListExtra("ingredients") != null)
        {
            List<String> ingredients = new ArrayList<>(getIntent().getStringArrayListExtra("ingredients"));
            presenter.getWithIngredients(database,ingredients);

        }
        else {
            presenter.getSearchableCollection(database);
        }

        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    void initvalues(SearchView searchView)
    {
        database = DBHelper.getInstance(this).getReadableDatabase();

        presenter = new SearchPresenter(searchView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initActionBar();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.push_out_to_bottom);
    }

    @Override
    public void showloading() {

    }

    @Override
    public void hideloading() {

    }

    @Override
    public void setSearchableCollection(List<Meals.Meal> meals,List<String> favlist) {
        adapter = new FilteredRecipesRecyclerViewAdapter(this,meals,favlist,database);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setClipToPadding(false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnitemClickListener(((view, position) -> {
            TextView mealname = view.findViewById(R.id.mealName);
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(EXTRA_DETAIL, mealname.getText().toString());
            startActivity(intent);
        }));

    }

    public static void addToFavorite(String id){
        FavoritesListHelper.addToFavorites(database,id);
    }

    public static void removeFromFavorite(String id){
        FavoritesListHelper.removeFromFavorites(database,id);
    }

    @Override
    public void setCollection(List<Meals.Meal> meals, int[] matching,List<String> favlist) {

        adapter = new FilteredRecipesRecyclerViewAdapter(this,meals,matching,favlist,database);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setClipToPadding(false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        if(meals.size()<1){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Уведомление")
                    .setMessage("Блюд, которые можно было бы приготовить из указанных вами ингредиентов не найдено.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
            dialog.show();
        }



        adapter.setOnitemClickListener(((view, position) -> {
            TextView mealname = view.findViewById(R.id.mealName);
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(EXTRA_DETAIL, mealname.getText().toString());
            intent.putExtra(EXTRA_INSTRUCTIONS, meals.get(position).getStrIngredients());
            startActivity(intent);
        }));
    }

    private void initActionBar(){
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onErrorLoading(String message) {
        ApiNDialogHelper.showDialogMessage(this, "Ошибка ", message);
    }
}