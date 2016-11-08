<#include "../_inc/_layout.html"/> 
<#macro script>
function trash(id){
	$.get("${CPATH}/admin/comment/trash?ucode=${ucode}&id="+id, function(result){
		if(result.errorCode > 0){
			alert(result.message);
		}else{
			location.reload();
		}
	});
}

function restore(id){
	$.get("${CPATH}/admin/comment/restore?ucode=${ucode}&id="+id, function(result){
		if(result.errorCode > 0){
			alert(result.message);
		}else{
			location.reload();
		}
	});
}

function pub(id){
	$.get("${CPATH}/admin/comment/pub?ucode=${ucode}&id="+id, function(result){
		if(result.errorCode > 0){
			alert(result.message);
		}else{
			location.reload();
		}
	});
}

function draft(id){
	$.get("${CPATH}/admin/comment/draft?ucode=${ucode}&id="+id, function(result){
		if(result.errorCode > 0){
			alert(result.message);
		}else{
			location.reload();
		}
	});
}

function del(id){
	$.get("${CPATH}/admin/comment/delete?ucode=${ucode}&id="+id, function(result){
		if(result.errorCode > 0){
			alert(result.message);
		}else{
			location.reload();
		}
	});
}

function reply(id){
 	layer.open({
			    type: 2,
			    title: '回复评论',
			    shadeClose: true,
			    shade: 0.8,
			    area: ['70%', '70%'],
			    content: '${CPATH}/admin/comment/reply_layer?id='+id,
			    end:function(){
			    	location.reload();
			    }
			}); 
 
 }
 
</#macro> 
<@layout active_id=p child_active_id=c>
<section class="content-header">
	<h1>所有内容</h1>
</section>

<!-- Main content -->
<section class="content">
	<div class="row content-row">
		<ul class="list-inline" style="float: left">
			<li class="all">
				<a class="current" href="${CPATH}/admin/comment?m=${m!}&p=${p!}&c=${c!}">
					全部 <span class="count">(${count!"0"})</span>
				</a>
				|
			</li>
			<li class="publish">
				<a href="${CPATH}/admin/comment?m=${m!}&p=${p!}&c=${c!}&s=normal">
					已发布 <span class="count">(${normal_count!"0"})</span>
				</a>
				|
			</li>
			<li class="publish">
				<a href="${CPATH}/admin/comment?m=${m!}&p=${p!}&c=${c!}&s=draft">
					待审核 <span class="count">(${draft_count!"0"})</span>
				</a>
				|
			</li>
			<li class="trash">
				<a href="${CPATH}/admin/comment?m=${m!}&p=${p!}&c=${c!}&s=delete">
					垃圾箱 <span class="count">(${delete_count!"0"})</span>
				</a>
			</li>
		</ul>



		<form class="form-horizontal" style="float: right">
			<div class="input-group input-group-sm">
				<input id="post-search-input" class="form-control  " type="search" value="" name="" placeholder="请输入关键词">&nbsp;&nbsp;
				<input id="search-submit" class="btn btn-default btn-sm" type="submit" value="搜索">
			</div>

		</form>
	</div>
	<div class="row content-row">
		<div class="jp-left ">
			<select class="form-control input-sm jp-width120">
				<option value="10">==批量操作==</option>
				<option value="25">删除</option>
				<option value="50">置顶</option>
			</select>
		</div>
		<div class="jp-left   ">
			<button class="btn btn-block btn-sm btn-default" type="button">应用</button>
		</div>
	</div>


	<div class="box ">

		<!-- /.box-header -->
		<div class="box-body jp-cancel-pad">
			<table class="table table-striped">
				<thead>
					<tr>
						<th style="width: 2%"><input name="dataItem" onclick="checkAll(this)" type="checkbox"></th>
						<th style="width: 10%">作者</th>
						<th style="width: 33%">内容</th>
						<th style="width: 15%">回复至</th>
						<th style="width: 10%"><i class="fa fa-commenting"></i></th>
						<th style="width: 10%">日期</th>
					</tr>
				</thead>
				<tbody>

					<#if page??> 
					<#list page.getList() as bean>
					<tr class="jp-onmouse">
						<td><input name="dataItem" type="checkbox"></td>
						<td><a href="${CPATH}/admin/user/edit?id=${bean.user_id!}&c=list&p=user"> ${(bean.user.username)!comment.author!} </a></td>
						<td><strong><a href="#">
									<span class="article-title">${bean.text!}</span>
								</a><#if "draft" == bean.status!>(待审核)</#if>
								</strong>
							<div class="jp-flash-comment">
								<p class="row-actions jp-cancel-pad">
									<#if "normal" == bean.status!>
									<span class="approve"><a class="vim-a" href="javascript:reply(${bean.id})">回复</a></span> 
									<span class="approve">|<a class="vim-a" href="${CPATH}/admin/comment/edit?id=${bean.id!}&p=${(p)!}&c=${(c)!}">编辑</a></span>
									<span class="spam">|<a class="vim-s vim-destructive" href="javascript:draft(${bean.id})">待审核</a></span>
									<span class="spam">|<a class="vim-s vim-destructive" href="javascript:trash(${bean.id})">垃圾箱</a></span> 
									<#elseif "draft" == bean.status!>
									<span class="approve"><a class="vim-a" href="javascript:reply(${bean.id})">回复</a></span> 
									<span class="approve">|<a class="vim-a" href="${CPATH}/admin/comment/edit?id=${bean.id!}&p=${(p)!}&c=${(c)!}">编辑</a></span>
									<span class="spam">|<a class="vim-s vim-destructive" href="javascript:pub(${bean.id})">允许发布</a></span> 
									<span class="spam">|<a class="vim-s vim-destructive" href="javascript:trash(${bean.id})">垃圾箱</a></span> 
									<#else>
									<span class="approve"> <a class="vim-a" href="javascript:restore(${bean.id})">放回草稿</a></span> 
									<span class="spam">|<a class="vim-s vim-destructive" href="javascript:del(${bean.id})">永久删除</a></span> 
									</#if>
								</p>
							</div>
							</td>

						<td>
						<a href="${bean.contentUrl!}" target="_blank">${bean.content_title!}</a>
						</td>
						<td>${bean.comment_count!}</td>
						<td>${bean.created!}</td>
					</tr>
					</#list> 
					</#if>


				</tbody>
				<tfoot>
					<tr>
						<th style="width: 2%"><input name="dataItem" onclick="checkAll(this)" type="checkbox"></th>
						<th style="width: 10%">作者</th>
						<th style="width: 33%">内容</th>
						<th style="width: 15%">回复至</th>
						<th style="width: 10%"><i class="fa fa-commenting"></i></th>
						<th style="width: 10%">日期</th>
					</tr>
				</tfoot>
			</table>
		</div>
		<!-- /.box-body -->
	</div>
	<!-- /.box -->
	<div class="cf">
		<div class="pull-right ">
		<#if page??> 
		<#include "../_inc/_paginate.html" /> 
		<@paginate currentPage=page.pageNumber totalPage=page.totalPage actionUrl="?p="+(p!)+"&c="+(c!)+"&m="+(m!)+"&t="+(t!)+"&s="+(s!)+"&k="+(k!)+"&pid="+(pid!)+"&cid="+(cid!)+"&page="/> 
		</#if>
		</div>
	</div>
</section>
<!-- /.content -->
</@layout>



