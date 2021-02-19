package com.example.client.ui.Settings;

public class FAQ {
    public String question,answer;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean expanded;
    public FAQ(String question,String answer){
        this.question=question;
        this.answer=answer;
        this.expanded=false;
    }
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }



}
