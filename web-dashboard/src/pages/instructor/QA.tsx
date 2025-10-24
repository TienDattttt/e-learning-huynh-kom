import { useState } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { MessageSquare, Clock, CheckCircle } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

interface Question {
  id: number;
  student: {
    name: string;
    initials: string;
  };
  course: string;
  lesson: string;
  question: string;
  answer?: string;
  timestamp: string;
  status: "pending" | "answered";
}

const mockQuestions: Question[] = [
  {
    id: 1,
    student: { name: "Alice Johnson", initials: "AJ" },
    course: "React Advanced Patterns",
    lesson: "Custom Hooks",
    question: "How do I create a custom hook for fetching data with error handling?",
    timestamp: "2024-10-23 14:30",
    status: "pending",
  },
  {
    id: 2,
    student: { name: "Bob Smith", initials: "BS" },
    course: "React Advanced Patterns",
    lesson: "Context API",
    question: "What's the difference between Context API and Redux?",
    answer:
      "Context API is built into React and great for simple state management. Redux is more powerful for complex apps with middleware support and time-travel debugging.",
    timestamp: "2024-10-22 10:15",
    status: "answered",
  },
  {
    id: 3,
    student: { name: "Charlie Brown", initials: "CB" },
    course: "JavaScript Fundamentals",
    lesson: "Promises & Async/Await",
    question: "Can you explain the difference between .then() and async/await?",
    timestamp: "2024-10-23 09:45",
    status: "pending",
  },
];

export default function QA() {
  const [questions, setQuestions] = useState<Question[]>(mockQuestions);
  const [replyText, setReplyText] = useState<{ [key: number]: string }>({});
  const { toast } = useToast();

  const handleReply = (questionId: number) => {
    const answer = replyText[questionId];
    if (!answer || answer.trim() === "") {
      toast({
        title: "Error",
        description: "Please enter a reply",
        variant: "destructive",
      });
      return;
    }

    setQuestions(
      questions.map((q) =>
        q.id === questionId ? { ...q, answer, status: "answered" as const } : q
      )
    );

    setReplyText({ ...replyText, [questionId]: "" });

    toast({
      title: "Reply sent",
      description: "Your answer has been sent to the student.",
    });
  };

  const pendingQuestions = questions.filter((q) => q.status === "pending");
  const answeredQuestions = questions.filter((q) => q.status === "answered");

  return (
    <DashboardLayout role="instructor">
      <div className="space-y-6 animate-fade-in">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Q&A Management</h2>
          <p className="text-muted-foreground">Answer student questions and provide feedback</p>
        </div>

        <div className="grid gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <MessageSquare className="h-5 w-5" />
                Pending Questions ({pendingQuestions.length})
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              {pendingQuestions.length === 0 ? (
                <div className="text-center py-12 text-muted-foreground">
                  <CheckCircle className="h-12 w-12 mx-auto mb-4 opacity-50" />
                  <p>All caught up! No pending questions.</p>
                </div>
              ) : (
                pendingQuestions.map((q) => (
                  <div key={q.id} className="p-4 border rounded-lg space-y-4 bg-card">
                    <div className="flex items-start gap-4">
                      <Avatar>
                        <AvatarFallback className="bg-primary text-primary-foreground">
                          {q.student.initials}
                        </AvatarFallback>
                      </Avatar>
                      <div className="flex-1 space-y-2">
                        <div className="flex items-center justify-between">
                          <div>
                            <p className="font-medium">{q.student.name}</p>
                            <p className="text-sm text-muted-foreground">
                              {q.course} • {q.lesson}
                            </p>
                          </div>
                          <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Clock className="h-4 w-4" />
                            {q.timestamp}
                          </div>
                        </div>
                        <div className="p-3 bg-muted rounded-lg">
                          <p>{q.question}</p>
                        </div>
                        <div className="space-y-2">
                          <Textarea
                            placeholder="Type your answer here..."
                            value={replyText[q.id] || ""}
                            onChange={(e) =>
                              setReplyText({ ...replyText, [q.id]: e.target.value })
                            }
                            rows={3}
                          />
                          <Button onClick={() => handleReply(q.id)}>Send Reply</Button>
                        </div>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <CheckCircle className="h-5 w-5" />
                Answered Questions ({answeredQuestions.length})
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              {answeredQuestions.map((q) => (
                <div key={q.id} className="p-4 border rounded-lg space-y-4 bg-card">
                  <div className="flex items-start gap-4">
                    <Avatar>
                      <AvatarFallback className="bg-muted text-muted-foreground">
                        {q.student.initials}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1 space-y-2">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="font-medium">{q.student.name}</p>
                          <p className="text-sm text-muted-foreground">
                            {q.course} • {q.lesson}
                          </p>
                        </div>
                        <Badge variant="secondary">Answered</Badge>
                      </div>
                      <div className="p-3 bg-muted rounded-lg">
                        <p className="text-sm font-medium mb-1">Question:</p>
                        <p>{q.question}</p>
                      </div>
                      <div className="p-3 bg-primary/10 rounded-lg border border-primary/20">
                        <p className="text-sm font-medium mb-1 text-primary">Your Answer:</p>
                        <p>{q.answer}</p>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
}
