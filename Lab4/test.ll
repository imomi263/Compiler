; ModuleID = 'module'
source_filename = "module"

@g_var = global i32 -2

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 1, i32* %a, align 4
  %a1 = load i32, i32* %a, align 4
  %tmp_ = sub nsw i32 0, %a1
  ret i32 %tmp_
}
