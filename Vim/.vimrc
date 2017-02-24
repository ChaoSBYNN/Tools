"ChaoS_Zhang Vim file"

"语法高亮"
syntax enable

syntax on

"配色"
colorscheme desert

"设置字体" 
set guifont=Consolas:h12:cANSI

"设置窗口大小"
set lines=40 columns=155

"显示行号"
set nu

"显示标尺"  
set ruler

"自动保存"
set autowrite

"突出显示当前行"
set cursorline

"设置魔术"
set magic

"输入的命令显示出来，看的清楚些"
set showcmd

"去掉输入错误的提示声音"
set noeb

"在处理未保存或只读文件的时候，弹出确认"
set confirm

"自动缩进"
set autoindent

set cindent

"Tab键的宽度
set tabstop=4

"统一缩进为4"
set softtabstop=4

set shiftwidth=4

"不要用空格代替制表符"
set noexpandtab

"搜索忽略大小写"
set ignorecase

"去掉讨厌的有关vi一致性模式，避免以前版本的一些bug和局限"
set nocompatible

"允许折叠"
set foldenable

"设置字符集"
set fencs=utf-8

set termencoding=utf-8

set encoding=utf-8

set fileencodings=utf-8

set fileencoding=utf-8

"语言设置"
set langmenu=zh_CN.UTF-8

set helplang=cn

"解决中文乱码问题
set fenc=chinese

 "处理文本中显示乱码
 set encoding=utf-8
 set fileencodings=utf-8,chinese,latin-1
 if has("win32")
 set fileencoding=chinese
 else
 set fileencoding=utf-8
endif

 "处理菜单及右键菜单乱码
 source $VIMRUNTIME/delmenu.vim
 source $VIMRUNTIME/menu.vim
   
"处理consle输出乱码
 language messages zh_CN.utf-8
"中文乱码结束

"设置当文件被改动时自动载入"

set autoread

"quickfix模式"

autocmd FileType c,cpp map <buffer> <leader><space> :w<cr>:make<cr>

"代码补全"

set completeopt=preview,menu 

"允许插件" 

filetype plugin on

im"

"语法高亮"
syntax enable

syntax on

"配色"
colorscheme desert

"设置字体" 
set guifont=Consolas:h12:cANSI

"设置窗口大小"
set lines=40 columns=155

"显示行号"
set nu

"显示标尺"  
set ruler

"自动保存"
set autowrite

"突出显示当前行"
set cursorline

"设置魔术"
set magic

"输入的命令显示出来，看的清楚些"
set showcmd

"去掉输入错误的提示声音"
set noeb

"在处理未保存或只读文件的时候，弹出确认"
set confirm

"自动缩进"
set autoindent

set cindent

"Tab键的宽度
set tabstop=4

"统一缩进为4"
set softtabstop=4

set shiftwidth=4

"不要用空格代替制表符"
set noexpandtab

"搜索忽略大小写"
set ignorecase

"去掉讨厌的有关vi一致性模式，避免以前版本的一些bug和局限"
set nocompatible

"允许折叠"
set foldenable

"设置字符集"
set fencs=utf-8

set termencoding=utf-8

set encoding=utf-8

set fileencodings=utf-8

set fileencoding=utf-8

"语言设置"
set langmenu=zh_CN.UTF-8

set helplang=cn

"解决中文乱码问题
set fenc=chinese

 "处理文本中显示乱码
 set encoding=utf-8
 set fileencodings=utf-8,chinese,latin-1
 if has("win32")
 set fileencoding=chinese
 else
 set fileencoding=utf-8
endif

 "处理菜单及右键菜单乱码
 source $VIMRUNTIME/delmenu.vim
 source $VIMRUNTIME/menu.vim
   
"处理consle输出乱码
 language messages zh_CN.utf-8
"中文乱码结束

"设置当文件被改动时自动载入"

set autoread

"quickfix模式"

autocmd FileType c,cpp map <buffer> <leader><space> :w<cr>:make<cr>

"代码补全"

set completeopt=preview,menu 

"允许插件" 

filetype plugin on

"语法高亮"
syntax enable

syntax on

"配色"
colorscheme desert

"设置字体" 
set guifont=Consolas:h12:cANSI

"设置窗口大小"
set lines=40 columns=155

"显示行号"
set nu

"显示标尺"  
set ruler

"自动保存"
set autowrite

"突出显示当前行"
set cursorline

"设置魔术"
set magic

"输入的命令显示出来，看的清楚些"
set showcmd

"去掉输入错误的提示声音"
set noeb

"在处理未保存或只读文件的时候，弹出确认"
set confirm

"自动缩进"
set autoindent

set cindent

"Tab键的宽度
set tabstop=4

"统一缩进为4"
set softtabstop=4

set shiftwidth=4

"不要用空格代替制表符"
set noexpandtab

"搜索忽略大小写"
set ignorecase

"去掉讨厌的有关vi一致性模式，避免以前版本的一些bug和局限"
set nocompatible

"允许折叠"
set foldenable

"设置字符集"
set fencs=utf-8

set termencoding=utf-8

set encoding=utf-8

set fileencodings=utf-8

set fileencoding=utf-8

"语言设置"
set langmenu=zh_CN.UTF-8

set helplang=cn

"解决中文乱码问题
set fenc=chinese

 "处理文本中显示乱码
 set encoding=utf-8
 set fileencodings=utf-8,chinese,latin-1
 if has("win32")
 set fileencoding=chinese
 else
 set fileencoding=utf-8
endif

 "处理菜单及右键菜单乱码
 source $VIMRUNTIME/delmenu.vim
 source $VIMRUNTIME/menu.vim
   
"处理consle输出乱码
 language messages zh_CN.utf-8
"中文乱码结束

"设置当文件被改动时自动载入"

set autoread

"quickfix模式"

autocmd FileType c,cpp map <buffer> <leader><space> :w<cr>:make<cr>

"代码补全"

set completeopt=preview,menu 

"允许插件" 

filetype plugin on

