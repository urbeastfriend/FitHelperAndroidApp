package pr.code.views.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pr.code.R;
import pr.code.adapters.FilteredRecipesRecyclerViewAdapter;
import pr.code.models.Meals;
import pr.code.utils.DBHelper;
import pr.code.views.recipedetails.DetailsActivity;

import static pr.code.views.recipes.RecipesFragment.EXTRA_DETAIL;

public class SearchActivity extends AppCompatActivity implements SearchView{

    @BindView(R.id.searchRecipesEditText)
    EditText editText;

    @BindView(R.id.searchRecyclerView)
    RecyclerView recyclerView;

    static SQLiteDatabase database;
    static SearchPresenter presenter;
    private FilteredRecipesRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initvalues(this);
        setTitle("Поиск рецептов");

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

    void initvalues(SearchView searchView)
    {
        database = DBHelper.getInstance(this).getReadableDatabase();
        presenter = new SearchPresenter(searchView);
    }

    @Override
    protected void onResume() {
        presenter.getSearchableCollection(database);
        super.onResume();
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
    public void setSearchableCollection(List<Meals.Meal> meals) {
        adapter = new FilteredRecipesRecyclerViewAdapter(this,meals);
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
        presenter.addToFavorites(database,id);
    }

    public static void removeFromFavorite(String id){
        presenter.removeFromFavorites(database,id);
    }
}