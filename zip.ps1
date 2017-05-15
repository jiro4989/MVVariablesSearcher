############################################################
# 使い方 |
#--------+--------------------------------------------------
# 1行目  | versionフォルダ名
# 2行目  | 出力zipフォルダ名
# 3行目~ | 取り込むファイルのパス
############################################################

try {
  # zip圧縮する対象を指定するファイルの存在のチェック
  $exists = test-path zip_target.txt
  if (!$exists) {
    $wsobj = new-object -comobject wscript.shell;
    $result = $wsobj.popup("zip_target.txtがありません。`nzip_target.txt内にはzip圧縮するファイルのパスのみを記述します。")
    exit
  }

  # テキストファイルをリストに取り込む
  $files = new-object 'system.collections.generic.list[string]'
  cat zip_target.txt | % {
    $files.add($_)
  }

  # バージョン情報、出力ファイルをリストから削除して取り込む
  $version = $files[0]
  $zipname = $files[1]
  $zipfile = ".\dist\" + $version + "\$zipname"
  $files.removeAt(0)
  $files.removeAt(0)

  # 出力先チェック
  $exists = test-path .\dist
  if (!$exists) {
    mkdir .\dist
  }

  # 出力先チェック
  $exists = test-path (".\dist\" + $version)
  if (!$exists) {
    mkdir (".\dist\" + $version)
  }

  # 同名のzipが存在したら削除
  $exists = test-path $zipfile
  if ($exists) {
    del $zipfile
  }

  # zip圧縮実行
  compress-archive -path $files -destination $zipfile
} catch [Exception] {
  $wsobj = new-object -comobject wscript.shell;
  $result = $wsobj.popup("何らかのエラーが発生しました。")
}

