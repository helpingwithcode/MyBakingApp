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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.model.Steps;

import java.util.List;

import io.realm.RealmResults;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.RecipeStepsAdapterViewHolder> {

    private final RecipeStepAdapterOnClick mClickHandler;
    private List<Steps> recipeSteps;

    public RecipeStepsAdapter(RecipeStepAdapterOnClick clickHandler, List<Steps> allSteps) {
        mClickHandler = clickHandler;
        recipeSteps = allSteps;
    }

    public interface RecipeStepAdapterOnClick {
        void thisClick(int thisStepId, int thisRecipeId);
    }

    public class RecipeStepsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView recipeStepDescription;
//        public final TextView recipeStepShortDescription;
        //public final TextView recipeIndex;
        private int thisStepId;
        private int thisRecipeId;

        public RecipeStepsAdapterViewHolder(View view) {
            super(view);
//            recipeIndex = view.findViewById(R.id.tv_step_index);
            recipeStepDescription = view.findViewById(R.id.tv_description);
            //recipeStepShortDescription = view.findViewById(R.id.tv_short_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.thisClick(this.thisStepId, this.thisRecipeId);
        }

        private void setThisStepId(int id, int recipeId) {
            this.thisRecipeId = recipeId;
            this.thisStepId = id;
        }
    }

    @Override
    public RecipeStepsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new RecipeStepsAdapterViewHolder(inflater.inflate(R.layout.item_recipe_step, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecipeStepsAdapterViewHolder holder, int position) {
        Steps step = recipeSteps.get(position);
        //holder.recipeStepShortDescription.setText(step.getShortDescription());
        holder.recipeStepDescription.setText(step.getDescription());
//        holder.recipeIndex.setText(String.valueOf(position));
        holder.setThisStepId(step.getId(),step.getRecipeId());
    }

    @Override
    public int getItemCount() {
        return (recipeSteps == null)?0: recipeSteps.size();
    }
}