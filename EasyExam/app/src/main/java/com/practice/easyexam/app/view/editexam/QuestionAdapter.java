package com.practice.easyexam.app.view.editexam;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Constants;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.model.Question;


public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_QUESTION = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_ANSWER = 2;
    private Context context;
    static InputAnswerAdapter inputAnswerAdapter;
    static SelectAnswerAdapter selectAnswerAdapter;
    String image = "";
    String question = "";
    int correctAns = 0;
    Question ques;

    SelectImage selectImage;
    Bitmap bitmap;

    public QuestionAdapter(Context context) {
        this.context = context;
    }

    public void setQues(Question ques) {
        this.ques = ques;
    }




    public void setSelectImage(SelectImage selectImage) {
        this.selectImage = selectImage;
    }

    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        notifyDataSetChanged();
    }

    public Bitmap getImageBitmap() {
        return bitmap;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_QUESTION:
                View viewA = inflater.inflate(R.layout.item_view_question, parent, false);
                return new QuestionViewHolder(viewA);
            case TYPE_IMAGE:
                View viewB = inflater.inflate(R.layout.item_view_image, parent, false);
                return new ImageViewHolder(viewB);
            case TYPE_ANSWER:
                View viewC = inflater.inflate(R.layout.item_view_answer, parent, false);
                return new AnswerViewHolder(viewC);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_QUESTION:
                QuestionViewHolder viewHolder0 = (QuestionViewHolder) holder;
                if (ques != null) {
                    viewHolder0.editText.setText(ques.getQuestion());
                }
                viewHolder0.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        question = charSequence.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        question = editable.toString();
                    }
                });
                question = String.valueOf(((QuestionViewHolder) holder).editText.getText());
                break;
            case TYPE_IMAGE:
                ImageViewHolder viewHolder1 = (ImageViewHolder) holder;
                if (bitmap != null) {
//                    int newHeight = 100; // Set your desired height
//                    int compressionQuality = 50; // Set your desired compression quality (0-100)
//                    int screenWidth = getScreenWidth();
//                    // Set newWidth to screen width - 20
//                    int newWidth = screenWidth - 15;
//                    String b64Image = Utils.getStringImage(bitmap);
//                    String compressedResizedBase64Image = Utils.compressAndResizeImage(b64Image, newWidth, newHeight, compressionQuality);
                    viewHolder1.imageView.setImageBitmap(bitmap);
                }
                if (Utils.isImageUrl(ques.getImage())) {
                    Glide.with(context)
                            .load(ques.getImage())
                            .into(viewHolder1.imageView);
                }
                viewHolder1.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectImage.onSelectImage();
                    }
                });
                viewHolder1.imgViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder1.imageView.setImageResource(R.drawable.ic_add);
                        ques.setImage("");
                    }
                });
                break;
            case TYPE_ANSWER:
                AnswerViewHolder viewHolder2 = (AnswerViewHolder) holder;

                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public int getItemViewType(int position) {
        // Determine the view type based on the position
        if (position == 0) {
            return TYPE_QUESTION;
        } else if (position == 1) {
            return TYPE_IMAGE;
        } else {
            return TYPE_ANSWER;
        }
    }

    public String getAllTextFromInputAnswers() {
        return inputAnswerAdapter.getAllText();
    }

    public String getQuestion() {
        return question;
    }

    public int getCorrectAns() {
        return selectAnswerAdapter.getSelectedItem();
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }



    public  class QuestionViewHolder extends RecyclerView.ViewHolder {
        EditText editText;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.edtQuestion);
        }

    }

    public  class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,imgViewDelete;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgSelectImage);
            imgViewDelete = itemView.findViewById(R.id.imgDeleteImage);
        }
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvInputAnswer;
        RecyclerView rvSelectAnswer;

        TextView tvAdd, tvSubtract;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            rvInputAnswer = itemView.findViewById(R.id.rvInputAnswer);
            rvSelectAnswer = itemView.findViewById(R.id.rvSelectAnswer);
            tvAdd = itemView.findViewById(R.id.tvAdd);
            tvSubtract = itemView.findViewById(R.id.tvSubtract);
            bindData(itemView);
        }

        void bindData(View itemView) {
            String[] arr = Utils.getCharArr();
            inputAnswerAdapter = new InputAnswerAdapter();
            rvInputAnswer.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvInputAnswer.setAdapter(inputAnswerAdapter);
            selectAnswerAdapter = new SelectAnswerAdapter(itemView.getContext());
            rvSelectAnswer.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvSelectAnswer.setAdapter(selectAnswerAdapter);
            if (ques != null) {
                inputAnswerAdapter.setAns(ques.getAnswers());
                selectAnswerAdapter.setItemCount(ques.getAnswers());
            }
            inputAnswerAdapter.notifyDataSetChanged();
            selectAnswerAdapter.notifyDataSetChanged();

            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputAnswerAdapter.addItem();
                    selectAnswerAdapter.addItem();
                }
            });

            tvSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputAnswerAdapter.subtractItem();
                    selectAnswerAdapter.subtractItem();
                }
            });

            selectAnswerAdapter.setOnSelectItem(new SelectAnswerAdapter.onSelectItem() {
                @Override
                public void onClickItem(int position) {
                    correctAns = position;
                }
            });
        }
    }

    public interface SelectImage {
        void onSelectImage();
    }

}

