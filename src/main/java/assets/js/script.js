/**
 * http://usejsdoc.org/
 */

$(function() {
	
	$('#checkBtn').click(function(){
		$.ajax({
		    type: "POST",
		    url: "/v1/pdrm/summon",
		    data: "ic_no=" + $("#icTxt").val(),
		    dataType: "json",
		    success: function (response) {
		    	var template = $.templates("#theTmpl");
		    	var htmlOutput = template.render(response.data.SummonData);
		    	$("#summonResult").html(htmlOutput);
		    	$("#summonResultTable").show();
		    }
		});
	});
	
	/*data = {
		"data" : {
			"Status" : true,
			"Author" : "alifaziz@gmail.com",
			"TotalAmount" : "600.00",
			"SummonData" : [ {
				"SummonsNo" : "02AL569818",
				"VehicleNo" : "WTS7636",
				"Blacklisted" : "YES",
				"OPSSikapEnforcement" : "NO",
				"Amount" : "300.00",
				"FinalAmount" : "300.00",
				"OriginalAmount" : "300.00",
				"OffenceDate" : "25-03-2012 13:40:00",
				"EnforcementDate" : "26-03-2012",
				"NonCompoundable" : "NO",
				"District" : "KELANG",
				"Offence" : "LOST CONTROL",
				"Location" : "JLN TELOK GADUNG"
			}, {
				"SummonsNo" : "02AR163733",
				"VehicleNo" : "BMD769",
				"Blacklisted" : "YES",
				"OPSSikapEnforcement" : "NO",
				"Amount" : "300.00",
				"FinalAmount" : "300.00",
				"OriginalAmount" : "300.00",
				"OffenceDate" : "07-07-2014 17:32:00",
				"EnforcementDate" : "08-07-2014",
				"NonCompoundable" : "NO",
				"District" : "IPD KLANG UTARA",
				"Offence" : "NO HELMET",
				"Location" : "LINGKARAN SULTAN ABD SAMAD"
			} ],
			"IcNumber" : "840116105325"
		}
	}
	
	var template = $.templates("#theTmpl");
	var htmlOutput = template.render(data.data.SummonData);
	$("#summonResult").html(htmlOutput);
	$("#summonResultTable").show();*/
});
