/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.helpingiwthcode.mybakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.model.Recipe;

import io.realm.RealmResults;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private final RecipeAdapterOnClick mClickHandler;
    private RealmResults<Recipe> recipes;

    public RecipeAdapter(RecipeAdapterOnClick clickHandler, RealmResults<Recipe> allRecipes) {
        mClickHandler = clickHandler;
        recipes = allRecipes;
    }

    public interface RecipeAdapterOnClick {
        void thisClick(int thisRecipeId);
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int thisMovieId;
        public final TextView recipeNameTv;
        private int thisRecipeId;

        public RecipeAdapterViewHolder(View view) {
            super(view);
            recipeNameTv = view.findViewById(R.id.tv_recipe_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.thisClick(this.thisRecipeId);
        }

        private void setThisRecipeId(int id) {
            this.thisRecipeId = id;
        }
    }

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_recipe;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeNameTv.setText(recipe.getName());
        holder.setThisRecipeId(recipe.getId());
    }

    @Override
    public int getItemCount() {
        return (recipes == null)?0: recipes.size();
    }
}