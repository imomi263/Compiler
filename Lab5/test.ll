; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %b = alloca i32, align 4
  store i32 0, i32* %b, align 4
  %a1 = load i32, i32* %a, align 4
  %t1 = icmp eq i32 %a1, 0
  br i1 %t1, label %lhs_false1, label %all_true2

lhs_false1:                                       ; preds = %mainEntry
  %b2 = load i32, i32* %b, align 4
  %t2 = icmp ne i32 %b2, 0
  br i1 %t2, label %true3, label %false4

all_true2:                                        ; preds = %mainEntry

true3:                                            ; preds = %lhs_false1
  %c = alloca i32, align 4
  store i32 1, i32* %c, align 4

false4:                                           ; preds = %lhs_false1
  br label %end5

end5:                                             ; preds = %false4
  ret i32 0
}
