package com.example.HWMobile_NoaGilboa;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ScoresFragment extends Fragment {

    private RecyclerView rvScores;
    private ScoreAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_fragment_scores, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvScores = view.findViewById(R.id.rvScores);
        rvScores.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Score> scores = MSPV3.getMe(requireContext()).getScores();
        ScoresActivity activity = (ScoresActivity) getActivity();
        System.out.println(scores.size());
        adapter = new ScoreAdapter(scores, activity);
        rvScores.setAdapter(adapter);
    }
}
