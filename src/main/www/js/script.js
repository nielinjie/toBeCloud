/* Author:

*/

$(function(){
	$('#peer-refresh-button').click(function() {
		peer_refresh();
		return false;
	})
});

function peer_refresh() {
	$.getJSON('/ui/peers', function(peers) {
		$('#peer-list').empty()
		$.each(peers.peers, function(index, peer) {
			peer_display(peer).appendTo('#peer-list')
		})
	});
}
function peer_display(peer) {
	var a = $("<a href='#'></a>").text(peer.peer).click(function() {
		diff_refresh(peer);
		return false
	})
	return $('<li></li>').append(a);
}
function diff_refresh(peer) {
	$.getJSON('/ui/diff',{"peer":peer.peer},function(diffs){
		$('#diff-list').empty()
		$.each(diffs.diff, function(index, diff){
			$('<li></li>').text(diff.relativePath).appendTo('#diff-list')
		})
	})
	
}
