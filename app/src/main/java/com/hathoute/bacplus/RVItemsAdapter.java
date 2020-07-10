package com.hathoute.bacplus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RVItemsAdapter extends RecyclerView.Adapter<RVItemsAdapter.MyViewHolder>
        implements View.OnClickListener {

    private List<Object> objectList;
    private RecyclerView mRecyclerView;
    private static final int VIEW_SECTION_MAIN = 1;
    private static final int VIEW_SECTION_SIGMA = 2;
    private static final int VIEW_NOTICE = 3;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvYear, tvSection, tvNotice;
        public ImageButton ibOpen, ibDownload, ibDelete;
        public ImageView ivNotice;

        public MyViewHolder(View view) {
            super(view);
            if(view.getTag().equals(VIEW_SECTION_MAIN) || view.getTag().equals(VIEW_SECTION_SIGMA))
                tvSection = view.findViewById(R.id.tvSection);
            else if(view.getTag().equals(VIEW_NOTICE)) {
                tvNotice = view.findViewById(R.id.tvNotice);
                ivNotice = view.findViewById(R.id.ivNotice);
            }
            else {
                tvName = view.findViewById(R.id.tvName);
                tvYear = view.findViewById(R.id.tvYear);
                ibOpen = view.findViewById(R.id.ibOpen);
                ibDownload = view.findViewById(R.id.ibDownload);
                ibDelete = view.findViewById(R.id.ibDelete);
            }
        }
    }


    public RVItemsAdapter(List<Object> objectList) {
        this.objectList = objectList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == VIEW_SECTION_MAIN)
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_section_main, parent, false);
        else if(viewType == VIEW_SECTION_SIGMA)
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_section_sigma, parent, false);
        else if(viewType == VIEW_NOTICE)
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notice, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_1, parent, false);

        itemView.setTag(viewType);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if(objectList.get(position) instanceof Integer) {
            if((Integer)objectList.get(position) < 0)
                return VIEW_SECTION_SIGMA;
            else if((Integer)objectList.get(position) == R.drawable.icon_nothingfound
                    || (Integer)objectList.get(position) == R.drawable.icon_emptylist)
                return VIEW_NOTICE;
            else
                return VIEW_SECTION_MAIN;
        }
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Object object = objectList.get(position);
        if(getItemViewType(position) == VIEW_SECTION_MAIN
                || getItemViewType(position) == VIEW_SECTION_SIGMA) {
            if(object instanceof Integer) {
                if((Integer)object < 0) {
                    int arrayID = (Integer) object * -1 - 1;
                    holder.tvSection.setText(App.getContext().getResources()
                            .getStringArray(R.array.subject_tabs)[arrayID]);
                }
                else
                    holder.tvSection.setText(App.getContext().getResources()
                            .getStringArray(R.array.subjects)[(Integer) object]);
            }
        }
        else if(getItemViewType(position) == VIEW_NOTICE && object instanceof Integer) {
            holder.tvNotice.setText((Integer)object == R.drawable.icon_emptylist ?
                    R.string.notice_empty_lastseen : R.string.notice_nothing_found_available);
            holder.ivNotice.setImageDrawable(App.getContext()
                    .getResources().getDrawable((Integer)object));
        }
        else {
            if (object instanceof Lesson) {
                holder.tvName.setText(((Lesson) object).getName());
                holder.tvYear.setText(AppHelper.getYearResbyId(((Lesson) object).getYear()));
            } else if (object instanceof Exam) {
                holder.tvName.setText(((Exam) object).formatName());
                holder.tvYear.setText(AppHelper.getYearResbyId(((Exam) object).getYear()));
            }

            holder.ibOpen.setTag(position);
            holder.ibOpen.setOnClickListener(this);
            holder.ibDownload.setTag(position);
            holder.ibDownload.setOnClickListener(this);
            holder.ibDelete.setTag(position);
            holder.ibDelete.setOnClickListener(this);
            ((View) holder.tvName.getParent()).setOnClickListener(this);

            int isAvailable;
            if (object instanceof Lesson)
                isAvailable = ((Lesson) object).isAvailable(App.getContext());
            else
                isAvailable = ((Exam) object).isAvailable(App.getContext());

            switch (isAvailable) {
                case AppHelper.Storage.None:
                case AppHelper.Storage.Cache:
                    //((LinearLayout) holder.ibDelete.getParent().getParent()).removeView((View) holder.ibDelete.getParent());
                    ((View) holder.ibDelete.getParent()).setVisibility(View.GONE);
                    ((View) holder.ibDownload.getParent()).setVisibility(View.VISIBLE);
                    break;
                case AppHelper.Storage.Data:
                    //((LinearLayout) holder.ibDownload.getParent().getParent()).removeView((View) holder.ibDownload.getParent());
                    ((View) holder.ibDownload.getParent()).setVisibility(View.GONE);
                    ((View) holder.ibDelete.getParent()).setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int position;
        try {
            position = mRecyclerView.getChildAdapterPosition(v);
        } catch (ClassCastException e) {
            position = (Integer) v.getTag();
        }

        Object object = objectList.get(position);
        Context context = v.getContext();

        switch(v.getId()) {
            case R.id.ibOpen:
                if(object instanceof Lesson) {
                    if (!((Lesson) object).open(context))
                        ((Lesson) object).download(context, true);
                }
                else {
                    if (!((Exam) object).open(context))
                        ((Exam) object).download(context, true);
                }
                break;
            case R.id.ibDownload:
                if(object instanceof Lesson)
                    ((Lesson) object).download(context, false);
                else
                    ((Exam) object).download(context, false);
                break;
            case R.id.ibDelete:
                if(object instanceof Lesson)
                    ((Lesson) object).deleteDialog(context);
                else
                    ((Exam) object).deleteDialog(context);
                break;
            default:
                if(object instanceof Lesson) {
                    Intent intent = new Intent(context, LessonActivity.class);
                    intent.putExtra("subject", ((Lesson) object).getSubject())
                            .putExtra("lesson", ((Lesson) object).getId());
                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, ExamActivity.class);
                    intent.putExtra("subject", ((Exam) object).getSubject())
                            .putExtra("exam", ((Exam) object).getId());
                    context.startActivity(intent);
                }
                break;
        }
    }

    public Object getObject(int position) {
        return objectList.get(position);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }
}
