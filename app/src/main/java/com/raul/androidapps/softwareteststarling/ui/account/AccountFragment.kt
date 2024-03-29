package com.raul.androidapps.softwareteststarling.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raul.androidapps.softwareteststarling.R
import com.raul.androidapps.softwareteststarling.databinding.AccountFragmentBinding
import com.raul.androidapps.softwareteststarling.extensions.getValueWithTwoDecimalsPrecisionInStringFormat
import com.raul.androidapps.softwareteststarling.ui.MainActivity
import com.raul.androidapps.softwareteststarling.ui.NetworkViewModel
import com.raul.androidapps.softwareteststarling.ui.common.BaseFragment
import kotlinx.coroutines.async


class AccountFragment : BaseFragment() {

    private lateinit var binding: AccountFragmentBinding

    private lateinit var viewModel: AccountViewModel
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var adapter: FeedsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.account_fragment,
            container,
            false,
            starlingBindingComponent
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AccountViewModel::class.java)
        networkViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NetworkViewModel::class.java)
        adapter = FeedsAdapter(resourcesManager = resourcesManager, starlingBindingComponent = starlingBindingComponent)
        binding.feedContainer.feedList.apply {
            adapter = this@AccountFragment.adapter
            val dividerItemDecoration = DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
        }
        binding.resources = resourcesManager

        (activity as? MainActivity)?.setBackArrow(false)

        binding.savingButton.setOnClickListener {
            val accountUid = viewModel.accounts.value?.firstOrNull()?.account?.accountUid
            val currency = viewModel.accounts.value?.firstOrNull()?.account?.currency
            if (accountUid != null && currency != null) {
                val direction = AccountFragmentDirections.actionAccountFragmentToSaveFragment(accountUid, currency)
                findNavController().navigate(direction)
            } else {
                Toast.makeText(context, resourcesManager.getString(R.string.invalid_account), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.accounts.observe(this.viewLifecycleOwner, Observer {
            it?.let { list ->
                //we are only reading the first account -> this app only handles one account per user
                list.firstOrNull()?.let { accountEncrypted ->
                    this.lifecycleScope.launchWhenCreated {
                        val identifiers = accountEncrypted.identifiers
                        //prepare and pass balance to binding
                        async {
                            viewModel.getBalanceFromEntity(accountEncrypted.balance.firstOrNull())
                                ?.amount?.let { amount ->
                                val value = amount.minorUnits.toFloat() / 100
                                binding.balance =
                                    "${value.getValueWithTwoDecimalsPrecisionInStringFormat()} ${amount.currency}"
                                binding.executePendingBindings()
                            }
                        }
                        //prepare and pass identifiers to binding
                        async {
                            binding.identifiers =
                                viewModel.getIdentifiersFromEntity(identifiers.firstOrNull(), encryption)
                            binding.executePendingBindings()
                        }
                        //prepare and pass feeds
                        async {
                            viewModel.getFeedsFromEntities(accountEncrypted.feeds)
                        }
                    }
                }
            }
        })
        viewModel.getListOfFeeds().observe(this.viewLifecycleOwner, Observer {
            it?.let {
                if(it.isNotEmpty()){
                    binding.feedContainer.loadingData.visibility = View.GONE
                }
                adapter.updateItems(it)
            }
        })

    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.getInfoFromServer()
    }


}
